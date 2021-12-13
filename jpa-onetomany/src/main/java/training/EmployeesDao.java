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

    public List<Employee> listEmployeesWithAddresses() {
        return entityManager.createQuery("select distinct e from Employee e left join fetch e.addresses")
                .setFirstResult(2)
                .setMaxResults(3)
                .getResultList();
    }

    public List<NamePairDto> listPairs() {
        // Projection query
        return entityManager.createQuery("""
                select new NamePairDto(e.name, a.city) from Employee e join fetch e.addresses a
                """, NamePairDto.class)
                .getResultList();
    }
}
