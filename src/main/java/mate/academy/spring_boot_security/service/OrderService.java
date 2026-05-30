package mate.academy.spring_boot_security.service;

import mate.academy.spring_boot_security.dto.order.CreateOrderRequestDto;
import mate.academy.spring_boot_security.dto.order.OrderDto;
import mate.academy.spring_boot_security.dto.order.OrderItemDto;
import mate.academy.spring_boot_security.dto.order.UpdateOrderStatusDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto placeOrder(Long userId, CreateOrderRequestDto requestDto);

    Page<OrderDto> getOrderHistory(Long userId, Pageable pageable);

    OrderDto updateStatus(Long id, UpdateOrderStatusDto requestDto);

    Page<OrderItemDto> getOrderItems(Long userId, Long orderId, Pageable pageable);

    OrderItemDto getOrderItemByOrderAndId(Long userId,  Long orderId, Long orderItemId);
}
