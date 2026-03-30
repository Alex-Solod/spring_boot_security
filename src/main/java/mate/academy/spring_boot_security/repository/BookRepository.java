package mate.academy.spring_boot_security.repository;

import mate.academy.spring_boot_security.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findAllByIsDeletedFalse(Pageable pageable);
}
