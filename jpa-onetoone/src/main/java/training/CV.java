package training;

import javax.persistence.*;

@Entity
@Table(name = "cv")
public class CV {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE) // Best practice: id mindig legyen sequence!
    private Long id;

    @Lob
    private String text;

    @OneToOne(mappedBy = "cv")
    private Employee employee;

    public CV() {
    }

    public CV(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
