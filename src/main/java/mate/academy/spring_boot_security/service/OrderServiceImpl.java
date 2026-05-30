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
import java.time.LocalDateTime;
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
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setShippingAddress(requestDto.getShippingAddress());
        order.setStatus(Order.Status.PENDING);
        order.setOrderDate(LocalDateTime.now());

        Set<OrderItem> orderItems = cart.getCartItems().stream().map(cartItem -> {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setBook(cartItem.getBook());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(cartItem.getBook().getPrice());
            return item;
        }).collect(Collectors.toSet());

        BigDecimal total = orderItems.stream().map(item -> item.getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setOrderItems(orderItems);
        order.setTotal(total);

        Order savedOrder = orderRepository.save(order);
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
        return orderItemRepository.findByOrderId(orderId, pageable).map(orderItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderItemDto getOrderItemByOrderAndId(Long userId, Long orderId, Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findByIdAndOrderId(orderItemId, orderId).orElseThrow(
                () -> new EntityNotFoundException("OrderItem with " + orderItemId + " not found"));
        return orderItemMapper.toDto(orderItem);
    }
}
