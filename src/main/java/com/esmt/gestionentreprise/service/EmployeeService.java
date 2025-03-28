package com.esmt.gestionentreprise.service;

import com.esmt.gestionentreprise.entities.Employee;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class EmployeeService {
    @PersistenceContext
    private EntityManager em;

    public void save(Employee employee) {
        em.persist(employee);
        em.flush();
    }

    public Employee findById(Long id) {
        return em.find(Employee.class, id);
    }

    public Employee findByEmail(String email) {
        try {
            return em.createQuery("SELECT e FROM Employee e WHERE e.email = :email", Employee.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Employee> findAll(int page, int size) {
        return em.createQuery("SELECT e FROM Employee e LEFT JOIN FETCH e.department", Employee.class)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Employee> findAll() {
        return em.createQuery("SELECT e FROM Employee e LEFT JOIN FETCH e.department", Employee.class)
                .getResultList();
    }

    public void update(Employee employee) {
        em.merge(employee);
        em.flush();
    }

    public void delete(Long id) {
        Employee employee = em.find(Employee.class, id);
        if (employee != null) {
            em.remove(employee);
            em.flush();
        }
    }

    public String getNomEtPrenomByEmail(String email){
        Employee employee = em.createQuery("SELECT e FROM Employee e WHERE e.email = :email", Employee.class)
                .setParameter("email", email)
                .getSingleResult();
        return employee.getPrenom() + " " + employee.getNom();
    }

    public String getRoleByEmail(String email){
        Employee employee = em.createQuery("SELECT e FROM Employee e WHERE e.email = :email", Employee.class)
                .setParameter("email", email)
                .getSingleResult();
        return employee.getRole();
    }

    public List<Employee> searchEmployees(String search) {
        return em.createQuery("SELECT e FROM Employee e LEFT JOIN e.department WHERE e.nom LIKE :search OR e.role LIKE :search OR e.department.name LIKE :search", Employee.class)
                .setParameter("search", "%" + search + "%")
                .getResultList();
    }

    public int getTotalEmployeesCount() {
        return ((Number) em.createQuery("SELECT COUNT(e) FROM Employee e").getSingleResult()).intValue();
    }

    public long getEmployeeCount() {
        return ((Number) em.createQuery("SELECT COUNT(e) FROM Employee e").getSingleResult()).longValue();
    }
}

