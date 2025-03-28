package com.esmt.gestionentreprise.service;

import com.esmt.gestionentreprise.entities.Department;
import com.esmt.gestionentreprise.entities.Employee;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class DepartmentService {
    private static final Logger LOGGER = Logger.getLogger(DepartmentService.class.getName());
    @PersistenceContext
    private EntityManager em;

    @Inject
    private EmailService emailService;

    public void addDepartment(Department department) {
        em.persist(department);
        em.flush();
    }

    public List<Department> getAllDepartments(int page, int size) {
        return em.createQuery("SELECT d FROM Department d", Department.class)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    public Department getDepartmentById(Long id) {
        return em.find(Department.class, id);
    }

    public void updateDepartment(Department department) {
        em.merge(department);
        em.flush();
    }

    public void removeDepartment(Long id) {
        Department department = em.find(Department.class, id);
        if (department != null) {
            em.remove(department);
            em.flush();
        }
    }

    public void assignEmployeesToDepartment(Long departmentId, List<Long> employeesIds) {
        Department department = em.find(Department.class, departmentId);
        if (department != null) {
            employeesIds.forEach(employeeId -> {
                Employee employee = em.find(Employee.class, employeeId);
                if (employee != null) {
                    if (employee.getDepartment() == null || !employee.getDepartment().getId().equals(departmentId)) {
                        employee.setDepartment(department);
                        em.merge(employee);

                        envoyerNotification(employee, department);
                    }
                }
            });
            em.flush();
        }
    }

    public List<Employee> getEmployeesByDepartment(Long departmentId) {
        return em.createQuery("SELECT e FROM Employee e WHERE e.department.id = :departmentId", Employee.class)
                .setParameter("departmentId", departmentId)
                .getResultList();
    }

    public int getTotalDepartmentsCount() {
        return em.createQuery("SELECT COUNT(d) FROM Department d", Long.class).getSingleResult().intValue();
    }

    public long getDepartmentCount() {
        return em.createQuery("SELECT COUNT(d) FROM Department d", Long.class).getSingleResult();
    }

    private void envoyerNotification(Employee employee, Department department) {
        String email = employee.getEmail();
        String sujet = "Affectation à un département";
        String message = "Bonjour " + employee.getNom() + ",\n\n" +
                "Vous avez été affecté au département " + department.getName() + ".\n\n" +
                "Cordialement,\nL'équipe RH";

        try {
            emailService.envoyerEmail(email, sujet, message);
            LOGGER.log(Level.INFO, "Email envoye a {0} pour l'affectation au departement {1}",
                    new Object[]{email, department.getName()});
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'envoi de l'email à " + email, e);
        }
    }
}
