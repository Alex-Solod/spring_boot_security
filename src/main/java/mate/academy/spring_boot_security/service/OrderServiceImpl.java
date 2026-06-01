package mate.academy.spring_boot_security.service;

import lombok.RequiredArgsConstructor;
import mate.academy.spring_boot_security.dto.order.CreateOrderRequestDto;
import mate.academy.spring_boot_security.dto.order.OrderDto;
import mate.academy.spring_boot_security.dto.order.OrderItemDto;
import mate.academy.spring_boot_security.dto.order.UpdateOrderStatusDto;
import mate.academy.spring_boot_security.exception.EntityNotFoundException;
import mate.academy.spring_boot_security.mapper.OrderItemMapper;
import mate.academy.spring_boot_security.mapper.OrderMapper;
import mate.academy.spring_boot_security.model.Order;
import mate.academy.spring_boot_security.model.OrderItem;
import mate.academy.spring_boot_security.model.ShoppingCart;
import mate.academy.spring_boot_security.repository.CartItemRepository;
import mate.academy.spring_boot_security.repository.OrderItemRepository;
import mate.academy.spring_boot_security.repository.OrderRepository;
import mate.academy.spring_boot_security.repository.ShoppingCartRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import org.springframework.security.access.AccessDeniedException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderDto placeOrder(Long userId, CreateOrderRequestDto requestDto) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart was not found for user with id:" + userId));

        Order order = orderMapper.toModel(requestDto, cart);

        Set<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = orderItemMapper.toModel(cartItem);
                    orderItem.setOrder(order);
                    return orderItem;
                }).collect(Collectors.toSet());

        BigDecimal total = calculateTotal(orderItems);

        order.setOrderItems(orderItems);
        order.setTotal(total);

        Order savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);
        cartItemRepository.deleteAll(cart.getCartItems());

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getOrderHistory(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable).map(orderMapper::toDto);
    }

    @Override
    public OrderDto updateStatus(Long id, UpdateOrderStatusDto requestDto) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Order with " + id + " not found"));
        order.setStatus(requestDto.getStatus());
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemDto> getOrderItems(Long userId, Long orderId, Pageable pageable) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Order with " + orderId + " not found"));
        if (!order.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You don't have permission to view this order");
        }
        return orderItemRepository.findByOrderId(orderId, pageable).map(orderItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderItemDto getOrderItemByOrderAndId(Long userId, Long orderId, Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findByIdAndOrderIdAndOrderUserId(orderItemId, orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order item with id " + orderItemId + " was not found for this order and user"));

        return orderItemMapper.toDto(orderItem);
    }

    private BigDecimal calculateTotal(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
