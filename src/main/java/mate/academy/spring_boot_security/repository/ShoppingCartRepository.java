package mate.academy.spring_boot_security.repository;

import mate.academy.spring_boot_security.model.ShoppingCart;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findById(Long id);

    Optional<ShoppingCart> findByUserId(Long userId);
}
