package com.esmt.gestionentreprise.servlets;

import com.esmt.gestionentreprise.entities.Project;
import com.esmt.gestionentreprise.service.ProjectService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "ProjectServlet", urlPatterns = "/projects")
public class ProjectServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ProjectServlet.class.getName());
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE = 1;
    @EJB
    private ProjectService projectService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
        String action = request.getParameter("action");

        if (action == null) {
                listProject(request, response);
            } else {
                switch (action) {
                    case "add":
                        request.getRequestDispatcher("/projects/projectForm.jsp").forward(request, response);
                        break;
                    case "edit":
                        showEditForm(request, response);
                        break;
                    default:
                        listProject(request, response);
                        break;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing task request", e);
            setErrorMessage(request, "Error processing request: " + e.getMessage());
            listProject(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {

            String action = request.getParameter("action");

            if (action == null) {
                response.sendRedirect(request.getContextPath() + "/tasks");
                return;
            }

            switch (action) {
                case "add":
                    addProjet(request, response);
                    break;
                case "edit":
                    editProject(request, response);
                    break;
                case "delete":
                    deleteProject(request, response);
                    break;
                case "assign":
                    assignEmployeesToProject(request, response);
                    break;
                default:
                    listProject(request, response);
                    break;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing employee request", e);
            setErrorMessage(request, "Error processing request: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/tasks");
        }
    }

    private void listProject(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int page = getValidatedPage(request);
        int pageSize = getValidatedPageSize(request);

        List<Project> projects = projectService.getAllProjects(page, pageSize);
        int totalProjects = projectService.getTotalProjectsCount();
        int totalPages = calculateTotalPages(totalProjects, pageSize);

        request.setAttribute("projects", projects);
        request.setAttribute("totalProjects", totalProjects);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("page", page);
        request.setAttribute("pageSize", pageSize);

        processSessionMessages(request);

        request.getRequestDispatcher("/projects/projects.jsp").forward(request, response);
    }

    private void deleteProject(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idParam = request.getParameter("id");

            if (idParam == null || idParam.isEmpty()) {
                throw new IllegalArgumentException("ID du projet manquant");
            }

            Long id = Long.parseLong(idParam);

            Project project = projectService.getProjectById(id);
            if (project == null) {
                setErrorMessage(request, "Projet non trouvé avec l'ID: " + id);
                response.sendRedirect(request.getContextPath() + "/projects");
                return;
            }
            projectService.deleteProject(id);

            setSuccessMessage(request, "Projet supprimé avec succès");
            response.sendRedirect(request.getContextPath() + "/projects");
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid project ID format", e);
            setErrorMessage(request, "Format d'ID invalide");
            response.sendRedirect(request.getContextPath() + "/project");
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                throw new IllegalArgumentException("ID du projet manquant");
            }

            Long id = Long.parseLong(idParam);
            Project project = projectService.getProjectById(id);

            if (project == null) {
                request.setAttribute("errorMessage", "Projet non trouvé avec l'ID: " + id);
                request.getRequestDispatcher("/projects").forward(request, response);
                return;
            }

            request.setAttribute("project", project);
            request.setAttribute("editMode", true);
            request.getRequestDispatcher("/projects/projectForm.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid Employee ID format", e);
            request.setAttribute("errorMessage", "Format d'ID invalide");
            request.getRequestDispatcher("/projects").forward(request, response);
        }
    }

    private void assignEmployeesToProject(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long projectId = Long.parseLong(request.getParameter("projectId"));
        Long employeeId = Long.parseLong(request.getParameter("employeeId"));

        projectService.assignEmployeesToProject(projectId, List.of(employeeId));

        response.sendRedirect("projects");
    }

    private void addProjet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nom = request.getParameter("nom");
        String description = request.getParameter("description");
        String statut = "EN COURS";
        LocalDate dateDebut = LocalDate.now();

        Project project = new Project(nom, description, dateDebut, statut);

        projectService.addProject(project);

        setSuccessMessage(request, "Projet ajouté avec succès");

        response.sendRedirect(request.getContextPath() + "/projects");
    }

    private void editProject(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        String nom = request.getParameter("nom");
        String description = request.getParameter("description");
        String status = request.getParameter("status");

        Project project = projectService.getProjectById(id);
        if (project == null) {
            response.sendRedirect(request.getContextPath() + "/projects?error=notFound");
            return;
        }

        project.setNom(nom);
        project.setDescription(description);
        switch (status) {
            case "En cours": project.setStatut("En cours"); break;
            case "En attente": project.setStatut("En attente"); break;
            case "Termine": project.setStatut("Termine"); break;
            case "Annule": project.setStatut("Annule"); break;
            case "Bloque": project.setStatut("Bloque"); break;
            default: project.setStatut("A faire");
        }
        if ("Termine".equals(status)) {
            project.setDateFin(LocalDate.now());
        }

        projectService.updateProject(project);

        setSuccessMessage(request, "Projet modifié avec succès");

        response.sendRedirect(request.getContextPath() + "/projects");
    }

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
