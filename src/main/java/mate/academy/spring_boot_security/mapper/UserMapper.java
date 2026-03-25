package mate.academy.spring_boot_security.mapper;

import mate.academy.spring_boot_security.dto.user.UserRegistrationRequestDto;
import mate.academy.spring_boot_security.dto.user.UserResponseDto;
import mate.academy.spring_boot_security.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    User toModel(UserRegistrationRequestDto requestDto);
}
