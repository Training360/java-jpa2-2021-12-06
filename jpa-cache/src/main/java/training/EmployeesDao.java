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

    public Employee findEmployeeById(long id) {
        return entityManager.find(Employee.class, id);
    }


    public List<Employee> listEmployees() {
        return entityManager.createQuery("select e from Employee e", Employee.class)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }
}
