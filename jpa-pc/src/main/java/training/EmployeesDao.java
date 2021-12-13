package training;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class EmployeesDao {

    private EntityManager entityManager;

    public EmployeesDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void saveEmployee(Employee employee) {
        entityManager.persist(employee);
        // employee.id
    }

    public List<Employee> listEmployees() {
        return entityManager.createQuery("select e from Employee e", Employee.class)
                .getResultList();
    }

    public Employee findEmployeeById(long id) {
        return entityManager.find(Employee.class, id);
    }

    // Hogy implementálunk update-et?
    public void updateName(long id, String newName) {
        Employee employee = entityManager.find(Employee.class, id);
        employee.setName(newName);
    }

    public void generateEmployees() {
        for (int i = 0; i < 100; i++) {
            entityManager.persist(new Employee("name " + i));
        }
    }
}
