package mate.academy.spring_boot_security.service;

import lombok.RequiredArgsConstructor;
import mate.academy.spring_boot_security.dto.user.UserRegistrationRequestDto;
import mate.academy.spring_boot_security.dto.user.UserResponseDto;
import mate.academy.spring_boot_security.exception.RegistrationException;
import mate.academy.spring_boot_security.mapper.UserMapper;
import mate.academy.spring_boot_security.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto registerUser(UserRegistrationRequestDto requestDto)
    {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("User with this email "
                    + requestDto.getEmail()
                    + " already exists");
        }

        return userMapper.toDto(
                userRepository.save(userMapper.toModel(requestDto)));
    }
}
