package training;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeesDaoTest {

    EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("pu");
    }

    @Test
    void testSaveAndFind() {
        doInJPA(() -> entityManagerFactory,
                (em) -> {
                    EmployeesDao dao = new EmployeesDao(em);
                    dao.saveEmployee(new Employee("John Doe"));

                    Employee employee = dao.findEmployeeById(1L);

                    assertEquals("John Doe", employee.getName());
                });

        doInJPA(() -> entityManagerFactory,
                (em) -> {
                    EmployeesDao dao = new EmployeesDao(em);
                    Employee employee = dao.findEmployeeById(1L);
                    assertEquals("John Doe", employee.getName());
                });
    }

    @Test
    void testSaveAndList() {
        long id = doInJPA(() -> entityManagerFactory,
                (em) -> {
                    EmployeesDao dao = new EmployeesDao(em);
                    Employee employee = new Employee("John Doe");
                    dao.saveEmployee(employee);
                    return employee.getId();
                });

        doInJPA(() -> entityManagerFactory,
                (em) -> {
                    EmployeesDao dao = new EmployeesDao(em);
                    System.out.println("Find employee");
                    Employee employee = dao.findEmployeeById(id);

                    assertEquals("John Doe", employee.getName());
                });

        doInJPA(() -> entityManagerFactory,
                (em) -> {
                    EmployeesDao dao = new EmployeesDao(em);
                    System.out.println("List employees");
                    List<Employee> employees = dao.listEmployees();

                    assertThat(employees)
                            .extracting(Employee::getName)
                            .containsExactly("John Doe");
                });

        doInJPA(() -> entityManagerFactory,
                (em) -> {
                    EmployeesDao dao = new EmployeesDao(em);
                    System.out.println("List employees");
                    List<Employee> employees = dao.listEmployees();

                    assertThat(employees)
                            .extracting(Employee::getName)
                            .containsExactly("John Doe");
                });
    }
}
