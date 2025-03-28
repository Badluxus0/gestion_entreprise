package com.esmt.gestionentreprise.servlets;

import com.esmt.gestionentreprise.entities.Task;
import com.esmt.gestionentreprise.service.TaskService;
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

@WebServlet(name = "TaskServlet", urlPatterns = "/tasks")
public class TaskServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(TaskServlet.class.getName());
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE = 1;
    @EJB
    private TaskService taskService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String action = request.getParameter("action");

            if (action == null) {
                listTask(request, response);
            } else {
                switch (action) {
                    case "add":
                        request.getRequestDispatcher("/tasks/taskForm.jsp").forward(request, response);
                        break;
                    case "edit":
                        showEditForm(request, response);
                        break;
                    default:
                        listTask(request, response);
                        break;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing task request", e);
            setErrorMessage(request, "Error processing request: " + e.getMessage());
            listTask(request, response);
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
                    addTask(request, response);
                    break;
                case "edit":
                    editTask(request, response);
                    break;
                case "delete":
                    deleteTask(request, response);
                    break;
                case "assign":
                    assignTaskToEmployee(request, response);
                    break;
                default:
                    listTask(request, response);
                    break;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing task request", e);
            setErrorMessage(request, "Error processing request: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/tasks");
        }
    }

    private void listTask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int page = getValidatedPage(request);
        int pageSize = getValidatedPageSize(request);

        List<Task> tasks = taskService.getAllTasks(page, pageSize);
        int totalTasks = taskService.getTotalProjectsCount();
        int totalPages = calculateTotalPages(totalTasks, pageSize);

        request.setAttribute("tasks", tasks);
        request.setAttribute("totalTasks", totalTasks);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("page", page);
        request.setAttribute("pageSize", pageSize);

        processSessionMessages(request);

        request.getRequestDispatcher("/tasks/tasks.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                throw new IllegalArgumentException("ID de la tache manquant");
            }

            Long id = Long.parseLong(idParam);
            Task task = taskService.getTaskById(id);

            if (task == null) {
                request.setAttribute("errorMessage", "Tache non trouvé avec l'ID: " + id);
                request.getRequestDispatcher("/tasks").forward(request, response);
                return;
            }

            request.setAttribute("task", task);
            request.setAttribute("editMode", true);
            request.getRequestDispatcher("/tasks/taskForm.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid Task ID format", e);
            request.setAttribute("errorMessage", "Format d'ID invalide");
            request.getRequestDispatcher("/tasks").forward(request, response);
        }
    }

    private void assignTaskToEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long taskId = Long.parseLong(request.getParameter("taskId"));
        Long employeeId = Long.parseLong(request.getParameter("employeeId"));

        taskService.assignTaskToEmployee(taskId, employeeId);

        response.sendRedirect("tasks");
    }

    private void addTask(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String description = request.getParameter("description");

        Task task = new Task();
        task.setDescription(description);
        task.setStatut("A faire");

        taskService.addTask(task);

        setSuccessMessage(request, "Tache ajouté avec succès");

        response.sendRedirect(request.getContextPath() + "/tasks");
    }

    private void deleteTask(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idParam = request.getParameter("id");

            if (idParam == null || idParam.isEmpty()) {
                throw new IllegalArgumentException("ID du projet manquant");
            }

            Long id = Long.parseLong(idParam);

            Task task = taskService.getTaskById(id);
            if (task == null) {
                setErrorMessage(request, "Tache non trouvé avec l'ID: " + id);
                response.sendRedirect(request.getContextPath() + "/tasks");
                return;
            }
            taskService.removeTask(id);

            setSuccessMessage(request, "Tache supprimé avec succès");
            response.sendRedirect(request.getContextPath() + "/tasks");
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid task ID format", e);
            setErrorMessage(request, "Format d'ID invalide");
            response.sendRedirect(request.getContextPath() + "/task");
        }
    }

    private void editTask(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        String description = request.getParameter("description");
        String status = request.getParameter("status");

        Task task = taskService.getTaskById(id);
        task.setDescription(description);

        switch (status) {
            case "En cours": task.setStatut("En cours"); break;
            case "En attente": task.setStatut("En attente"); break;
            case "Termine": task.setStatut("Termine"); break;
            case "Annule": task.setStatut("Annule"); break;
            case "Bloque": task.setStatut("Bloque"); break;
            default: task.setStatut("A faire");
        }

        taskService.updateTask(task);

        setSuccessMessage(request, "Tache modifié avec succès");

        response.sendRedirect(request.getContextPath() + "/tasks");
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
