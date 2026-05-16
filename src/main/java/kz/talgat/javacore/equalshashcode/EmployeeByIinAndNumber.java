package kz.talgat.javacore.equalshashcode;

import java.util.Objects;

/**
 * Намеренно неправильный наследник для демонстрации нарушения symmetry.
 * <p>
 * Родитель сравнивает только ИИН, а наследник сравнивает ИИН и табельный
 * номер. В реальном коде такую модель equality лучше не использовать.
 */
public final class EmployeeByIinAndNumber extends PersonByIin {

    private final String employeeNumber;

    public EmployeeByIinAndNumber(String iin, String employeeNumber) {
        super(iin);
        this.employeeNumber = employeeNumber;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EmployeeByIinAndNumber employee)) {
            return false;
        }
        return Objects.equals(getIin(), employee.getIin())
                && Objects.equals(employeeNumber, employee.employeeNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIin(), employeeNumber);
    }
}
