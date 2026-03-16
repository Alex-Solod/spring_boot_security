package mate.academy.spring_boot_security.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;
import java.util.Objects;

public class PasswordMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(final FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(final Object value,
                           final ConstraintValidatorContext context) {
        try {
            BeanWrapperImpl beanWrapper = new BeanWrapperImpl(value);

            Object firstObj = beanWrapper.getPropertyValue(firstFieldName);
            Object secondObj = beanWrapper.getPropertyValue(secondFieldName);

            return Objects.equals(firstObj, secondObj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
