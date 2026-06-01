package mate.academy.spring_boot_security.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import mate.academy.spring_boot_security.model.Order;

@Data
public class UpdateOrderStatusDto {
    @NotNull
    private Order.Status status;
}
