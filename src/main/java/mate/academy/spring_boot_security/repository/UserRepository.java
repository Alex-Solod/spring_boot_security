package mate.academy.spring_boot_security.repository;

import mate.academy.spring_boot_security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
