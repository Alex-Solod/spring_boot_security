package mate.academy.spring_boot_security.repository;

import mate.academy.spring_boot_security.model.Book;
import mate.academy.spring_boot_security.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Find only non-deleted books")
    void findAllByIsDeletedFalse_booksExist_returnsOnlyNotDeletedBooks() {
        //Given
        Book book = new Book();
        book.setTitle("TestTitle");
        book.setAuthor("TestAuthor");
        book.setIsbn("4761376479316");
        book.setPrice(BigDecimal.ONE);
        book.setDeleted(false);

        Book deletedBook = new Book();
        deletedBook.setTitle("TestTitle2");
        deletedBook.setAuthor("TestAuthor2");
        deletedBook.setIsbn("7761347931664");
        deletedBook.setPrice(BigDecimal.TWO);
        deletedBook.setDeleted(true);

        Book savedBook = bookRepository.save(book);
        bookRepository.save(deletedBook);

        //When
        Page<Book> result = bookRepository.findAllByIsDeletedFalse(Pageable.unpaged());

        //Then
        List<Book> content = result.getContent();

        assertEquals(1, content.size());
        assertEquals(savedBook.getId(), content.getFirst().getId());
        assertFalse(content.getFirst().isDeleted());
    }

    @Test
    @DisplayName("Find books by category ID")
    void findAllByCategoriesId_validCategoryId_returnsBooks() {
        //Given
        Category category = new Category();
        category.setName("TestName");
        category.setDeleted(false);
        categoryRepository.save(category);

        Book book = new Book();
        book.setTitle("TestTitle");
        book.setAuthor("TestAuthor");
        book.setIsbn("4761376479316");
        book.setPrice(BigDecimal.ONE);
        book.setDeleted(false);
        book.setCategories(Set.of(category));

        Book savedBook = bookRepository.save(book);

        //When
        Page<Book> result = bookRepository.findAllByCategories_Id(
                category.getId(), Pageable.unpaged());

        //Then
        assertFalse(result.getContent().isEmpty());
        assertEquals(1, result.getContent().size());

        Book actualBook = result.getContent().getFirst();
        assertEquals(savedBook.getId(), actualBook.getId());
    }
}
