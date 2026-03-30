package mate.academy.spring_boot_security.service;

import lombok.RequiredArgsConstructor;
import mate.academy.spring_boot_security.dto.user.UserRegistrationRequestDto;
import mate.academy.spring_boot_security.dto.user.UserResponseDto;
import mate.academy.spring_boot_security.exception.RegistrationException;
import mate.academy.spring_boot_security.mapper.UserMapper;
import mate.academy.spring_boot_security.model.Role;
import mate.academy.spring_boot_security.model.User;
import mate.academy.spring_boot_security.repository.RoleRepository;
import mate.academy.spring_boot_security.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("User with this email "
                    + requestDto.getEmail()
                    + " already exists");
        }

        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        Role userRole = roleRepository.findByName(Role.RoleName.USER)
                .orElseThrow(() -> new RegistrationException(
                        "Can't find role by name"));
        user.setRoles(Set.of(userRole));

        return userMapper.toDto(userRepository.save(user));
    }
}
