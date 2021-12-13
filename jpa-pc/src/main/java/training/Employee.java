package training;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employees_generator")
    @GenericGenerator(
            name = "employees_generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "location_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "20"),
                    @Parameter(name = "optimizer", value = "pooled-lo")
            })
//    @GeneratedValue(strategy = GenerationType.SEQUENCE) // Best practice: id mindig legyen sequence!
    private Long id; // Best practice: wrapper

    private String name;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String cv;

    public Employee() {
    }

    public Employee(String name) {
        this.name = name;
    }

    public Employee(String name, String cv) {
        this.name = name;
        this.cv = cv;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCv() {
        return cv;
    }

    public void setCv(String cv) {
        this.cv = cv;
    }
}
