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

                    for (int i = 0; i < 10; i++) {
                        Employee employee = new Employee("John Doe");
                        Address address1 = new Address("Budapest");
                        employee.addAddress(address1);
                        Address address2 = new Address("Szeged");
                        employee.addAddress(address2);
                        dao.saveEmployee(employee);
                    }
                });


        doInJPA(() -> entityManagerFactory,
                (em) -> {
                    EmployeesDao dao = new EmployeesDao(em);
                    List<Employee> employees = dao.listEmployeesWithAddresses();
                    System.out.println(employees.size());
                    for (Employee employee: employees) {
                        for (Address address : employee.getAddresses()) {
                            System.out.println(address.getCity());
                        }
                    }

                });
    }
}
