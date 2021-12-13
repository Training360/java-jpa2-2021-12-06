package training;

import javax.persistence.*;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE) // Best practice: id mindig legyen sequence!
    private Long id; // Best practice: wrapper

    private String name;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private CV cv;

    public Employee() {
    }

    public void addCV(CV cv) {
        this.cv = cv;
        cv.setEmployee(this);
    }

    public void removeCV() {
        cv.setEmployee(null);
        cv = null;
    }

    public Employee(String name) {
        this.name = name;
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

    public CV getCv() {
        return cv;
    }

    public void setCv(CV cv) {
        this.cv = cv;
    }
}
