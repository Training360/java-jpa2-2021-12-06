package training;

import lombok.AllArgsConstructor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@AllArgsConstructor
public class EmployeesDao {

    private EntityManager entityManager;

    public void saveEmployee(Employee employee) {
        entityManager.persist(employee);
        // employee.id
    }

    public List<Employee> listEmployees() {
        return entityManager.createQuery("select e from Employee e", Employee.class)
                .getResultList();
    }
}
