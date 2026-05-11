package mate.academy.spring_boot_security.mapper;

import mate.academy.spring_boot_security.dto.cartItem.CartItemDto;
import mate.academy.spring_boot_security.dto.cartItem.CartItemRequestDto;
import mate.academy.spring_boot_security.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = BookMapper.class)
public interface CartItemMapper {
    @Mapping(source = "bookId", target = "book", qualifiedByName = "bookFromId")
    CartItem toEntity(CartItemRequestDto cartItemDto);

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    CartItemDto toDto(CartItem cartItem);

    void updateCartFromDto(CartItemRequestDto cartItemDto,
                           @MappingTarget CartItem cartItem);
}
