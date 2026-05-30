package mate.academy.spring_boot_security.mapper;

import mate.academy.spring_boot_security.dto.order.OrderDto;
import mate.academy.spring_boot_security.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    OrderDto toDto(Order order);
}
