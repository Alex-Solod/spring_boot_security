package mate.academy.spring_boot_security.dto.order;

import lombok.Data;
import mate.academy.spring_boot_security.model.Order;

@Data
public class UpdateOrderStatusDto {
    private Order.Status status;
}
