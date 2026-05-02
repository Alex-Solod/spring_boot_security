package mate.academy.spring_boot_security.repository;

import mate.academy.spring_boot_security.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
