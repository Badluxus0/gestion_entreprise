package com.esmt.gestionentreprise.servlets;

import com.esmt.gestionentreprise.entities.Employee;
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

@WebServlet(name = "EmployeeServlet", value = "/employees")
public class EmployeeServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(EmployeeServlet.class.getName());
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE = 1;
    @EJB
    private EmployeeService employeeService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String action = request.getParameter("action");

            if (action == null) {
                listEmployees(request, response);
            } else {
                switch (action) {
                    case "add":
                        request.getRequestDispatcher("/employees/employeeForm.jsp").forward(request, response);
                        break;
                    case "edit":
                        showEditForm(request, response);
                        break;
                    case "search":
                        searchEmployee(request, response);
                        break;
                    default:
                        listEmployees(request, response);
                        break;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing employee request", e);
            setErrorMessage(request, "Error processing request: " + e.getMessage());
            listEmployees(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {

            String action = request.getParameter("action");

            if (action == null) {
                response.sendRedirect(request.getContextPath() + "/employees");
                return;
            }

            switch (action) {
                case "add":
                    addEmployee(request, response);
                    break;
                case "edit":
                    editEmployee(request, response);
                    break;
                case "delete":
                    deleteEmployee(request, response);
                    break;
                case "search":
                    searchEmployee(request, response);
                    break;
                default:
                    listEmployees(request, response);
                    break;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing employee request", e);
            setErrorMessage(request, "Error processing request: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/employees");
        }
    }

    private void searchEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String search = request.getParameter("search");
            if (search != null && !search.isEmpty()) {
                List<Employee> employees = employeeService.searchEmployees(search);

                request.setAttribute("employees", employees);
                request.setAttribute("totalEmployees", employees.size());
                request.setAttribute("totalPages", 1);
                request.setAttribute("page", 1);
                request.setAttribute("pageSize", employees.size());
                request.setAttribute("searchTerm", search);

                processSessionMessages(request);
                request.getRequestDispatcher("/employees/employees.jsp").forward(request, response);
            } else {
                // Si la recherche est vide, rediriger vers la liste complète
                response.sendRedirect(request.getContextPath() + "/employees");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error searching employees", e);
            setErrorMessage(request, "Erreur lors de la recherche: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/employees");
        }
    }

    private void deleteEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idParam = request.getParameter("id");

            if (idParam == null || idParam.isEmpty()) {
                throw new IllegalArgumentException("ID de département manquant");
            }

            Long id = Long.parseLong(idParam);

            Employee employee = employeeService.findById(id);
            if (employee == null) {
                setErrorMessage(request, "Employé non trouvé avec l'ID: " + id);
                response.sendRedirect(request.getContextPath() + "/employees");
                return;
            }
            employeeService.delete(id);

            setSuccessMessage(request, "Employé supprimé avec succès");
            response.sendRedirect(request.getContextPath() + "/employees");
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid department ID format", e);
            setErrorMessage(request, "Format d'ID invalide");
            response.sendRedirect(request.getContextPath() + "/employees");
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                throw new IllegalArgumentException("ID de l'employé manquant");
            }

            Long id = Long.parseLong(idParam);
            Employee employee = employeeService.findById(id);

            if (employee == null) {
                request.setAttribute("errorMessage", "Employee non trouvé avec l'ID: " + id);
                request.getRequestDispatcher("/employees").forward(request, response);
                return;
            }

            request.setAttribute("employee", employee);
            request.setAttribute("editMode", true);
            request.getRequestDispatcher("/employees/employeeForm.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid Employee ID format", e);
            request.setAttribute("errorMessage", "Format d'ID invalide");
            request.getRequestDispatcher("/employees").forward(request, response);
        }
    }

    private void listEmployees(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int page = getValidatedPage(request);
        int pageSize = getValidatedPageSize(request);

        List<Employee> employees = employeeService.findAll(page, pageSize);
        int totalEmployees = employeeService.getTotalEmployeesCount();
        int totalPages = calculateTotalPages(totalEmployees, pageSize);

        request.setAttribute("employees", employees);
        request.setAttribute("totalEmployees", totalEmployees);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("page", page);
        request.setAttribute("pageSize", pageSize);

        processSessionMessages(request);

        request.getRequestDispatcher("/employees/employees.jsp").forward(request, response);
    }

    private void addEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Récupérer les données du formulaire
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String email = request.getParameter("email");
        String role = request.getParameter("role");

        if (validateForm(request, response, nom, prenom, email, role)) return;

        // Créer un nouvel employé
        Employee employee = new Employee(nom, prenom, email, null, role);

        // Ajouter l'employé à la base de données
        employeeService.save(employee);

        // Ajouter un message de succès
        setSuccessMessage(request, "Employé ajouté avec succès");

        // Rediriger vers la liste des employés
        response.sendRedirect(request.getContextPath() + "/employees");
    }

    private void editEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Récupérer les données du formulaire
            String idParam = request.getParameter("id");
            String nom = request.getParameter("nom");
            String prenom = request.getParameter("prenom");
            String email = request.getParameter("email");
            String role = request.getParameter("role");

            if (idParam == null || idParam.isEmpty()) {
                throw new IllegalArgumentException("ID de l'employé manquant");
            }

            if (validateForm(request, response, nom, prenom, email, role)) return;

            Long id = Long.parseLong(idParam);

            // Récupérer l'employé à modifier
            Employee employee = employeeService.findById(id);

            // Mettre à jour les données de l'employé
            employee.setNom(nom);
            employee.setPrenom(prenom);
            employee.setEmail(email);
            employee.setRole(role);

            // Mettre à jour l'employé dans la base de données
            employeeService.update(employee);

            setSuccessMessage(request, "Employé mis à jour avec succès");

            // Rediriger vers la liste des employés
            response.sendRedirect(request.getContextPath() + "/employees");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error editing employee", e);
            setErrorMessage(request, "Error editing employee: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/employees");
        }
    }

    private boolean validateForm(HttpServletRequest request, HttpServletResponse response, String nom, String prenom, String email, String role) throws IOException {
        if (nom == null || nom.isEmpty() || prenom == null || prenom.isEmpty() || email == null || email.isEmpty() || role == null || role.isEmpty()) {
            setErrorMessage(request, "Veuillez remplir tous les champs");
            response.sendRedirect(request.getContextPath() + "/employees");
            return true;
        }
        return false;
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
