package mate.academy.spring_boot_security.service;

import mate.academy.spring_boot_security.dto.book.BookDto;
import mate.academy.spring_boot_security.dto.book.CreateBookRequestDto;
import mate.academy.spring_boot_security.dto.book.UpdateBookRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    Page<BookDto> getAllBooks(Pageable pageable);

    BookDto getBookById(Long id);

    BookDto save(CreateBookRequestDto requestDto);

    BookDto updateBook(Long id, UpdateBookRequestDto book);

    void deleteBook(Long id);
}
