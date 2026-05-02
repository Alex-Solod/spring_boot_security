package mate.academy.spring_boot_security.repository;

import mate.academy.spring_boot_security.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findAllByIsDeletedFalse(Pageable pageable);

    Page<Book> findAllByCategories_Id(Long categoryId, Pageable pageable);
}
