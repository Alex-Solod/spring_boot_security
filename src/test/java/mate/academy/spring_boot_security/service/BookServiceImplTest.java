package mate.academy.spring_boot_security.service;

import mate.academy.spring_boot_security.dto.book.BookDto;
import mate.academy.spring_boot_security.dto.book.CreateBookRequestDto;
import mate.academy.spring_boot_security.exception.EntityNotFoundException;
import mate.academy.spring_boot_security.mapper.BookMapper;
import mate.academy.spring_boot_security.model.Book;
import mate.academy.spring_boot_security.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void getBookById_validId_returnBookDto() {
        //Given
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);

        BookDto bookDto = new BookDto();
        bookDto.setId(bookId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        //When
        BookDto result = bookService.getBookById(bookId);

        //Then
        assertEquals(bookDto, result);
    }

    @Test
    void getBookById_invalidId_throwsException() {
        //Given
        Long bookId = 999L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        //When and Then
        assertThrows(EntityNotFoundException.class,
                () -> bookService.getBookById(bookId));
        verify(bookRepository).findById(bookId);
    }

    @Test
    void save_validRequest_returnsBookDto() {
        //Given
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        Book book = new Book();
        BookDto bookDto = new BookDto();

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        //When
        BookDto result = bookService.save(requestDto);

        //Then
        assertEquals(bookDto, result);
        verify(bookMapper).toModel(requestDto);
        verify(bookRepository).save(book);
        verify(bookMapper).toDto(book);
    }

    @Test
    void deleteById_existingBook_callsRepositoryDelete() {
        //Given
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        //When
        bookService.deleteBook(bookId);

        //Then
        assertTrue(book.isDeleted());
        verify(bookRepository).findById(bookId);
        verify(bookRepository).save(book);
    }

    @Test
    void deleteBook_invalidId_throwsEntityNotFoundException() {
        //Given
        Long bookId = 999L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        //Then
        assertThrows(EntityNotFoundException.class, () -> {
           bookService.deleteBook(bookId);
        });
        verify(bookRepository).findById(bookId);
        verify(bookRepository, never()).save(any(Book.class));
    }
}
