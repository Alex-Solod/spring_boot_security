package mate.academy.spring_boot_security.service;

import lombok.RequiredArgsConstructor;
import mate.academy.spring_boot_security.dto.book.BookDto;
import mate.academy.spring_boot_security.dto.book.CreateBookRequestDto;
import mate.academy.spring_boot_security.dto.book.UpdateBookRequestDto;
import mate.academy.spring_boot_security.exception.EntityNotFoundException;
import mate.academy.spring_boot_security.mapper.BookMapper;
import mate.academy.spring_boot_security.model.Book;
import mate.academy.spring_boot_security.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    @Override
    public Page<BookDto> getAllBooks(Pageable pageable) {
        return bookRepository.findAllByIsDeletedFalse(pageable).map(bookMapper::toDto);
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Book with id " + id + " not found"));
        return bookMapper.toDto(book);
    }

    @Override
    public BookDto updateBook(Long id, UpdateBookRequestDto bookRequestDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Book with id " + id + " not found"));
        bookMapper.updateBookFromDto(bookRequestDto, book);

        Book savedBook = bookRepository.save(book);
        return bookMapper.toDto(savedBook);
    }

    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Book with id " + id + " not found"));
        book.setDeleted(true);
        bookRepository.save(book);
    }
}

