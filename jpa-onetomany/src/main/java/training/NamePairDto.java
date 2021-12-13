package training;

public class NamePairDto {

    private String employeeName;

    private String city;

    public NamePairDto(String employeeName, String city) {
        this.employeeName = employeeName;
        this.city = city;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getCity() {
        return city;
    }
}
