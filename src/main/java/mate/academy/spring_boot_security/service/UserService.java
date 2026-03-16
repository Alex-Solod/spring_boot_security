package mate.academy.spring_boot_security.service;


import mate.academy.spring_boot_security.dto.UserRegistrationRequestDto;
import mate.academy.spring_boot_security.dto.UserResponseDto;

public interface UserService {
    UserResponseDto registerUser(UserRegistrationRequestDto requestDto);
}
