package training;

import com.p6spy.engine.spy.P6DataSource;
import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;
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

    ProxyTestDataSource dataSource;

    @BeforeEach
    void init() {
        // H2-t indít
        JdbcDataSource targetDataSource = new JdbcDataSource();
        targetDataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");


//        P6DataSource dataSource = new P6DataSource(targetDataSource);

        dataSource = new ProxyTestDataSource(targetDataSource);


        Map<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.nonJtaDataSource", dataSource);

        entityManagerFactory = Persistence.createEntityManagerFactory("pu", properties);


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
        System.out.println("Number of queries: "+ dataSource.getQueryExecutions().size());
        assertTrue(dataSource.getQueryExecutions().size() < 10);
    }
}
