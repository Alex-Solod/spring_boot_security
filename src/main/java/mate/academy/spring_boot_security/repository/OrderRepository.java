package mate.academy.spring_boot_security.repository;

import mate.academy.spring_boot_security.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = "orderItems")
    Page<Order> findByUserId(Long id, Pageable pageable);
}
