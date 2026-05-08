package mate.academy.spring_boot_security.repository;

import mate.academy.spring_boot_security.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
