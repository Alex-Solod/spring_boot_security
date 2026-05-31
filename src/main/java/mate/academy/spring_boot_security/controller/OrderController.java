package mate.academy.spring_boot_security.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mate.academy.spring_boot_security.dto.order.CreateOrderRequestDto;
import mate.academy.spring_boot_security.dto.order.OrderDto;
import mate.academy.spring_boot_security.dto.order.OrderItemDto;
import mate.academy.spring_boot_security.dto.order.UpdateOrderStatusDto;
import mate.academy.spring_boot_security.model.User;
import mate.academy.spring_boot_security.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

@Tag(name = "Order", description = "The list of orders")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public OrderDto placeOrder(Authentication authentication,
                               @RequestBody CreateOrderRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return orderService.placeOrder(user.getId(), requestDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Page<OrderDto> getOrderHistory(Authentication authentication, Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return orderService.getOrderHistory(user.getId(), pageable);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderDto updateStatus(@PathVariable Long id,
                                       @RequestBody UpdateOrderStatusDto updateOrderStatusDto) {
        return orderService.updateStatus(id, updateOrderStatusDto);
    }

    @GetMapping("/{orderId}/item")
    @PreAuthorize("hasRole('USER')")
    public Page<OrderItemDto>  getOrderItems(Authentication authentication,
                                             @PathVariable Long ordersId,
                                             Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return orderService.getOrderItems(user.getId(), ordersId, pageable);
    }

    @GetMapping("/{orderId}/item/{itemsId}")
    @PreAuthorize("hasRole('USER')")
    public OrderItemDto getOrderItemByOrderAndId(Authentication authentication,
                                                 @PathVariable Long ordersId,
                                                 @PathVariable Long itemsId) {
        User user = (User) authentication.getPrincipal();
        return orderService.getOrderItemByOrderAndId(user.getId(), ordersId, itemsId);
    }
}
