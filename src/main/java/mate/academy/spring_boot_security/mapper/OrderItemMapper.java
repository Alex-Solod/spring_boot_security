package mate.academy.spring_boot_security.mapper;

import mate.academy.spring_boot_security.dto.order.OrderItemDto;
import mate.academy.spring_boot_security.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    OrderItemDto toDto(OrderItem orderItem);
}
