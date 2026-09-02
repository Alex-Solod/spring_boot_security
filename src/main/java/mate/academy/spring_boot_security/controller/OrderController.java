package mate.academy.spring_boot_security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.spring_boot_security.dto.order.CreateOrderRequestDto;
import mate.academy.spring_boot_security.dto.order.OrderDto;
import mate.academy.spring_boot_security.dto.order.OrderItemDto;
import mate.academy.spring_boot_security.dto.order.UpdateOrderStatusDto;
import mate.academy.spring_boot_security.model.User;
import mate.academy.spring_boot_security.service.OrderService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order", description = "The list of orders")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Place an order")
    public OrderDto placeOrder(@Parameter(hidden = true) Authentication authentication,
                               @RequestBody @Valid CreateOrderRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return orderService.placeOrder(user.getId(), requestDto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get order history")
    public Page<OrderDto> getOrderHistory(@Parameter(hidden = true) Authentication authentication,
                                          @ParameterObject Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return orderService.getOrderHistory(user.getId(), pageable);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update order status")
    public OrderDto updateStatus(@PathVariable Long id,
                                 @RequestBody @Valid
                                 UpdateOrderStatusDto updateOrderStatusDto) {
        return orderService.updateStatus(id, updateOrderStatusDto);
    }

    @GetMapping("/{orderId}/item")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get all order items")
    public Page<OrderItemDto> getOrderItems(@Parameter(hidden = true) Authentication authentication,
                                            @PathVariable Long orderId,
                                            @ParameterObject Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return orderService.getOrderItems(user.getId(), orderId, pageable);
    }

    @GetMapping("/{orderId}/item/{itemsId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get order item by id")
    public OrderItemDto getOrderItemByOrderAndId(@Parameter(hidden = true) Authentication authentication,
                                                 @PathVariable Long orderId,
                                                 @PathVariable Long itemsId) {
        User user = (User) authentication.getPrincipal();
        return orderService.getOrderItemByOrderAndId(user.getId(), orderId, itemsId);
    }
}
