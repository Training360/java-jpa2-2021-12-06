package training;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeesDaoTest {

    EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("pu");
    }

    @Test
    void testSaveAndList() {
        doInJPA(() -> entityManagerFactory,
                (em) -> {
                    EmployeesDao dao = new EmployeesDao(em);
                    dao.saveEmployee(new Employee("John Doe"));

                    List<Employee> employees = dao.listEmployees();

                    assertThat(employees)
                            .extracting(Employee::getName)
                            .containsExactly("John Doe");
                });
    }

    @Test
    void testUpdate() {
        doInJPA(() -> entityManagerFactory,
                (em) -> {
                    EmployeesDao dao = new EmployeesDao(em);
                    Employee employee = new Employee("John Doe");
                    dao.saveEmployee(employee);

                    System.out.println(employee.getId());
                    dao.updateName(employee.getId(), "Jack Doe");

                    dao.updateName(employee.getId(), "John Doe");

                    List<Employee> employees = dao.listEmployees();
                    assertThat(employees)
                            .extracting(Employee::getName)
                            .containsExactly("John Doe");

                });
    }

    @Test
    void testFindById() {
        doInJPA(() -> entityManagerFactory,
                (em) -> {
                    EmployeesDao dao = new EmployeesDao(em);
                    Employee employee = new Employee("John Doe");
                    dao.saveEmployee(employee);

                    System.out.println("First query");
                    System.out.println(dao.findEmployeeById(employee.getId()));

                    System.out.println("Second query");
                    System.out.println(dao.findEmployeeById(employee.getId()));
                });
    }

    @Test
    void testFindByIdInTwoPhase() {
        doInJPA(() -> entityManagerFactory,
                (em) -> {
                    EmployeesDao dao = new EmployeesDao(em);
                    Employee employee = new Employee("John Doe");
                    dao.saveEmployee(employee);

                });

        doInJPA(() -> entityManagerFactory,
                (em) -> {
                    EmployeesDao dao = new EmployeesDao(em);
                    System.out.println(dao.findEmployeeById(1L));

                });
    }

    @Test
    void testBasicLazy() {
        doInJPA(() -> entityManagerFactory,
                (em) -> {
                    EmployeesDao dao = new EmployeesDao(em);
                    Employee employee = new Employee("John Doe", "programmer");
                    dao.saveEmployee(employee);

                });

        doInJPA(() -> entityManagerFactory,
                (em) -> {
                    EmployeesDao dao = new EmployeesDao(em);
                    List<Employee> employees = dao.listEmployees();

                    System.out.println(employees.get(0).getCv());
                });
    }

    @Test
    void testIdGenerator() {
        doInJPA(() -> entityManagerFactory,
                (em) -> {
                    EmployeesDao dao = new EmployeesDao(em);
                    dao.generateEmployees();
                });

    }
}
