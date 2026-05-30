package mate.academy.spring_boot_security.dto.order;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long id;
    private Long bookId;
    private int quantity;
}
