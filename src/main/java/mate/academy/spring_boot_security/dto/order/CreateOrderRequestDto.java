package mate.academy.spring_boot_security.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrderRequestDto {
    @NotBlank(message = "Shipping address cannot be empty")
    private String shippingAddress;
}
