package com.esmt.gestionentreprise.service;

import com.esmt.gestionentreprise.entities.Project;
import com.esmt.gestionentreprise.entities.Employee;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ProjectService {
    @PersistenceContext
    private EntityManager em;

    // Ajouter un projet
    public void addProject(Project project) {
        em.persist(project);
        em.flush();
    }

    // Modifier les détails d'un projet
    public void updateProject(Project project) {
        em.merge(project);
        em.flush();
    }

    // Récupérer un projet par son ID
    public Project getProjectById(Long id) {
        return em.find(Project.class, id);
    }

    // Lister tous les projets
    public List<Project> getAllProjects() {
        return em.createQuery("SELECT p FROM Project p", Project.class).getResultList();
    }

    public List<Project> getAllProjects(int page, int size) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        return em.createQuery("SELECT p FROM Project p", Project.class)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    // Lister les projets en cours
    public List<Project> getOngoingProjects() {
        return em.createQuery("SELECT p FROM Project p WHERE p.statut = 'En cours'", Project.class)
                .getResultList();
    }

    // Lister les projets terminés
    public List<Project> getCompletedProjects() {
        return em.createQuery("SELECT p FROM Project p WHERE p.statut = 'Termine'", Project.class)
                .getResultList();
    }

    // Supprimer un projet
    public void deleteProject(Long id) {
        Project project = em.find(Project.class, id);
        if (project != null) {
            em.remove(project);
            em.flush();
        }
    }

    // Assigner des employés à un projet
    public void assignEmployeesToProject(Long projectId, List<Long> employeeIds) {
        Project project = em.find(Project.class, projectId);
        if (project != null) {
            for (Long empId : employeeIds) {
                Employee employee = em.find(Employee.class, empId);
                if (employee != null) {
                    project.getEmployees().add(employee);
                    employee.getProjects().add(project);
                    em.merge(employee);
                }
            }
            em.merge(project);
            em.flush();
        }
    }

    public int getTotalProjectsCount() {
        return ((Number) em.createQuery("SELECT COUNT(p) FROM Project p").getSingleResult()).intValue();
    }

    public long getProjectCount() {
        return (Long) em.createQuery("SELECT COUNT(p) FROM Project p WHERE p.statut = 'EN COURS'").getSingleResult();
    }
}
