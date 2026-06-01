package mate.academy.spring_boot_security.repository;

import mate.academy.spring_boot_security.model.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Page<OrderItem> findByOrderId(Long orderId, Pageable pageable);

    Optional<OrderItem> findByIdAndOrderId(Long itemId, Long orderId);

    Optional<OrderItem> findByIdAndOrderIdAndOrderUserId(Long id, Long orderId, Long userId);
}
