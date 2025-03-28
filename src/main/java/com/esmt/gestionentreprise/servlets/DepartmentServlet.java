package com.esmt.gestionentreprise.servlets;

import com.esmt.gestionentreprise.entities.Department;
import com.esmt.gestionentreprise.entities.Employee;
import com.esmt.gestionentreprise.service.DepartmentService;
import com.esmt.gestionentreprise.service.EmployeeService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "DepartmentServlet", value = "/departments")
public class DepartmentServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(DepartmentServlet.class.getName());
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE = 1;

    @EJB
    private DepartmentService departmentService;

    @EJB
    private EmployeeService employeeService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {

            String action = request.getParameter("action");

            if (action == null) {
                listDepartments(request, response);
            } else {
                switch (action) {
                    case "edit":
                        showEditForm(request, response);
                        break;
                    case "viewEmployees":
                        viewEmployees(request, response);
                        break;
                    case "add":
                        showNewForm(request, response);
                        break;
                    default:
                        listDepartments(request, response);
                        break;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing department request", e);
            request.setAttribute("errorMessage", "Une erreur est survenue: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String action = request.getParameter("action");

            if (action == null) {
                response.sendRedirect(request.getContextPath() + "/departments");
                return;
            }

            switch (action) {
                case "add":
                    addDepartment(request, response);
                    break;
                case "edit":
                    updateDepartment(request, response);
                    break;
                case "delete":
                    deleteDepartment(request, response);
                    break;
                case "assign":
                    assignEmployeeToDepartment(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/departments");
                    break;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing department form submission", e);
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Une erreur est survenue: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/departments");
        }
    }

    private void listDepartments(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Parse pagination parameters with validation
        int page = getValidatedPage(request);
        int size = getValidatedPageSize(request);

        // Get data
        List<Department> departments = departmentService.getAllDepartments(page, size);
        List<Employee> employees = employeeService.findAll();

        // Calculate pagination info
        int totalDepartments = departmentService.getTotalDepartmentsCount();
        int totalPages = calculateTotalPages(totalDepartments, size);

        // Set attributes
        request.setAttribute("departments", departments);
        request.setAttribute("employees", employees);
        request.setAttribute("page", page);
        request.setAttribute("size", size);
        request.setAttribute("totalDepartments", totalDepartments);
        request.setAttribute("totalPages", totalPages);

        // Handle session messages
        processSessionMessages(request);

        // Forward to JSP
        request.getRequestDispatcher("/departments/departments.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                throw new IllegalArgumentException("ID de département manquant");
            }

            Long id = Long.parseLong(idParam);
            Department department = departmentService.getDepartmentById(id);

            if (department == null) {
                request.setAttribute("errorMessage", "Département non trouvé avec l'ID: " + id);
                request.getRequestDispatcher("/departments").forward(request, response);
                return;
            }

            request.setAttribute("department", department);
            request.setAttribute("editMode", true);
            request.getRequestDispatcher("/departments/departmentForm.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid department ID format", e);
            request.setAttribute("errorMessage", "Format d'ID invalide");
            request.getRequestDispatcher("/departments").forward(request, response);
        }
    }

    private void viewEmployees(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                throw new IllegalArgumentException("ID de département manquant");
            }

            Long departmentId = Long.parseLong(idParam);
            Department department = departmentService.getDepartmentById(departmentId);

            if (department == null) {
                request.setAttribute("errorMessage", "Département non trouvé avec l'ID: " + departmentId);
                request.getRequestDispatcher("/departments").forward(request, response);
                return;
            }

            List<Employee> employees = departmentService.getEmployeesByDepartment(departmentId);

            request.setAttribute("department", department);
            request.setAttribute("employees", employees);
            request.getRequestDispatcher("/departments/viewEmployees.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid department ID format", e);
            request.setAttribute("errorMessage", "Format d'ID invalide");
            request.getRequestDispatcher("/departments").forward(request, response);
        }
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("editMode", false);
        request.getRequestDispatcher("/departments/departmentForm.jsp").forward(request, response);
    }

    private void addDepartment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // Récupérer et valider les données du formulaire
        String name = request.getParameter("name");
        String description = request.getParameter("description");

        if (name == null || name.trim().isEmpty()) {
            setErrorMessage(request, "Le nom du département est obligatoire");
            response.sendRedirect(request.getContextPath() + "/departments/departmentForm.jsp");
            return;
        }

        // Créer et sauvegarder le département
        Department department = new Department(name, description);
        departmentService.addDepartment(department);

        // Message de succès et redirection
        setSuccessMessage(request, "Département ajouté avec succès");
        response.sendRedirect(request.getContextPath() + "/departments");
    }

    private void updateDepartment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            // Récupérer et valider les données du formulaire
            String idParam = request.getParameter("id");
            String name = request.getParameter("name");
            String description = request.getParameter("description");

            if (idParam == null || idParam.isEmpty()) {
                throw new IllegalArgumentException("ID de département manquant");
            }
            if (name == null || name.trim().isEmpty()) {
                setErrorMessage(request, "Le nom du département est obligatoire");
                response.sendRedirect(request.getContextPath() + "/departments?departmentForm.jsp");
                return;
            }

            Long id = Long.parseLong(idParam);

            // Créer et mettre à jour le département
            Department department = new Department(id, name, description);
            departmentService.updateDepartment(department);

            // Message de succès et redirection
            setSuccessMessage(request, "Département mis à jour avec succès");
            response.sendRedirect(request.getContextPath() + "/departments");
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid department ID format", e);
            setErrorMessage(request, "Format d'ID invalide");
            response.sendRedirect(request.getContextPath() + "/departments");
        }
    }

    private void deleteDepartment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String idParam = request.getParameter("id");

            if (idParam == null || idParam.isEmpty()) {
                throw new IllegalArgumentException("ID de département manquant");
            }

            Long id = Long.parseLong(idParam);

            // Vérifier si le département existe
            Department department = departmentService.getDepartmentById(id);
            if (department == null) {
                setErrorMessage(request, "Département non trouvé avec l'ID: " + id);
                response.sendRedirect(request.getContextPath() + "/departments");
                return;
            }

            // Supprimer le département
            departmentService.removeDepartment(id);

            // Message de succès et redirection
            setSuccessMessage(request, "Département supprimé avec succès");
            response.sendRedirect(request.getContextPath() + "/departments");
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid department ID format", e);
            setErrorMessage(request, "Format d'ID invalide");
            response.sendRedirect(request.getContextPath() + "/departments");
        }
    }

    private void assignEmployeeToDepartment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String departmentIdParam = request.getParameter("departmentId");
            String employeeIdParam = request.getParameter("employeeId");

            if (departmentIdParam == null || departmentIdParam.isEmpty()) {
                throw new IllegalArgumentException("ID de département manquant");
            }
            if (employeeIdParam == null || employeeIdParam.isEmpty()) {
                setErrorMessage(request, "Veuillez sélectionner un employé");
                response.sendRedirect(request.getContextPath() + "/departments");
                return;
            }

            Long departmentId = Long.parseLong(departmentIdParam);
            Long employeeId = Long.parseLong(employeeIdParam);

            // Vérifier si le département et l'employé existent
            Department department = departmentService.getDepartmentById(departmentId);
            Employee employee = employeeService.findById(employeeId);

            if (department == null) {
                setErrorMessage(request, "Département non trouvé avec l'ID: " + departmentId);
                response.sendRedirect(request.getContextPath() + "/departments");
                return;
            }
            if (employee == null) {
                setErrorMessage(request, "Employé non trouvé avec l'ID: " + employeeId);
                response.sendRedirect(request.getContextPath() + "/departments");
                return;
            }

            // Assigner l'employé au département
            departmentService.assignEmployeesToDepartment(departmentId, List.of(employeeId));

            // Message de succès et redirection
            setSuccessMessage(request, "Employé assigné avec succès au département");
            response.sendRedirect(request.getContextPath() + "/departments");
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid ID format", e);
            setErrorMessage(request, "Format d'ID invalide");
            response.sendRedirect(request.getContextPath() + "/departments");
        }
    }

    // Utility methods
    private int getValidatedPage(HttpServletRequest request) {
        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.isEmpty()) {
                int page = Integer.parseInt(pageParam);
                return page > 0 ? page : DEFAULT_PAGE;
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid page number format, using default", e);
        }
        return DEFAULT_PAGE;
    }

    private int getValidatedPageSize(HttpServletRequest request) {
        try {
            String sizeParam = request.getParameter("size");
            if (sizeParam != null && !sizeParam.isEmpty()) {
                int size = Integer.parseInt(sizeParam);
                return (size > 0 && size <= 100) ? size : DEFAULT_PAGE_SIZE;
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid page size format, using default", e);
        }
        return DEFAULT_PAGE_SIZE;
    }

    private int calculateTotalPages(int totalItems, int pageSize) {
        return (int) Math.ceil((double) totalItems / pageSize);
    }

    private void processSessionMessages(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            if (session.getAttribute("successMessage") != null) {
                request.setAttribute("successMessage", session.getAttribute("successMessage"));
                session.removeAttribute("successMessage");
            }
            if (session.getAttribute("errorMessage") != null) {
                request.setAttribute("errorMessage", session.getAttribute("errorMessage"));
                session.removeAttribute("errorMessage");
            }
        }
    }

    private void setSuccessMessage(HttpServletRequest request, String message) {
        HttpSession session = request.getSession();
        session.setAttribute("successMessage", message);
    }

    private void setErrorMessage(HttpServletRequest request, String message) {
        HttpSession session = request.getSession();
        session.setAttribute("errorMessage", message);
    }
}