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
                    Employee employee = new Employee("John Doe");
                    CV cv = new CV("programmer");
                    employee.addCV(cv);

                    System.out.println(cv.getEmployee());

                    dao.saveEmployee(employee);

                    List<Employee> employees = dao.listEmployees();

                    assertThat(employees)
                            .extracting(Employee::getName)
                            .containsExactly("John Doe");
                });
    }
}
