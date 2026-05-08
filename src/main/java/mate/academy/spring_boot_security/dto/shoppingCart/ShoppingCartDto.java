package mate.academy.spring_boot_security.dto.shoppingCart;

import lombok.Data;
import mate.academy.spring_boot_security.dto.cartItem.CartItemDto;
import java.util.Set;

@Data
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private Set<CartItemDto> cartItems;
}
