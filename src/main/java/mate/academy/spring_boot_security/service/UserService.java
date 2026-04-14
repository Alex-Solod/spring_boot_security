package mate.academy.spring_boot_security.service;


import mate.academy.spring_boot_security.dto.user.UserRegistrationRequestDto;
import mate.academy.spring_boot_security.dto.user.UserResponseDto;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto);
}
