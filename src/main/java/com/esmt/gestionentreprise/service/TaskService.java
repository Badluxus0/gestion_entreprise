package com.esmt.gestionentreprise.service;

import com.esmt.gestionentreprise.entities.Employee;
import com.esmt.gestionentreprise.entities.Task;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class TaskService {
    @PersistenceContext
    private EntityManager em;

    // Ajouter une tâche
    public void addTask(Task task) {
        em.persist(task);
        em.flush();
    }

    // Modifier une tâche
    public void updateTask(Task task) {
        em.merge(task);
        em.flush();
    }

    // Supprimer une tâche
    public void removeTask(Long id) {
        Task task = em.find(Task.class, id);
        if (task != null) {
            em.remove(task);
            em.flush();
        }
    }

    // Récupérer une tâche par son ID
    public Task getTaskById(Long id) {
        return em.createQuery("SELECT t FROM Task t LEFT JOIN FETCH t.employee LEFT JOIN FETCH t.project WHERE t.id = :id", Task.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    // Lister toutes les tâches
    public List<Task> getAllTasks() {
        return em.createQuery("SELECT t FROM Task t", Task.class).getResultList();
    }

    public void updateTaskStatus(Long taskId, String newStatus) {
        Task task = em.find(Task.class, taskId);
        if (task != null) {
            task.setStatut(newStatus);
            em.merge(task);
            em.flush();
        }
    }

    public List<Task> getAllTasks(int page, int limit) {
        if (page < 1) page = 1;
        if (limit < 1) limit = 10;
        return em.createQuery("SELECT t FROM Task t", Task.class).setFirstResult((page - 1) * limit).setMaxResults(limit).getResultList();
    }

    // Lister les tâches assignées à un employé
    public List<Task> getTasksByEmployee(Long employeeId) {
        return em.createQuery("SELECT t FROM Task t WHERE t.employee.id = :employeeId", Task.class).setParameter("employeeId", employeeId).getResultList();
    }

    // Lister les tâches par projet
    public List<Task> getTasksByProject(Long projectId) {
        return em.createQuery("SELECT t FROM Task t WHERE t.project.id = :projectId", Task.class).setParameter("projectId", projectId).getResultList();
    }

    // Assigner une tâche à un employé
    public void assignTaskToEmployee(Long taskId, Long employeeId) {
        Task task = em.find(Task.class, taskId);
        Employee employee = em.find(Employee.class, employeeId);
        if (task != null && employee != null) {
            task.setEmployee(employee);
            em.merge(task);
            em.flush();
        }
    }

    public int getTotalProjectsCount() {
        return em.createQuery("SELECT COUNT(t) FROM Task t", Long.class).getSingleResult().intValue();
    }

    public long getTasksInProgressCount() {
        return em.createQuery("SELECT COUNT(t) FROM Task t WHERE t.statut = 'EN COURS'", long.class).getSingleResult();
    }
}
