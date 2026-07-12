package mate.academy.spring_boot_security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.spring_boot_security.dto.book.BookDto;
import mate.academy.spring_boot_security.dto.book.CreateBookRequestDto;
import mate.academy.spring_boot_security.dto.book.UpdateBookRequestDto;
import mate.academy.spring_boot_security.model.Book;
import mate.academy.spring_boot_security.repository.BookRepository;
import mate.academy.spring_boot_security.repository.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    private static final Long BOOK_ID = 1L;
    private static final String BOOK_TITLE = "TestBook";
    private static final String BOOK_AUTHOR = "TestAuthor";
    private static final String ISBN = "123456789";
    private static final BigDecimal PRICE = new BigDecimal("9.99");

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
            ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Find all Books")
    @WithMockUser(username = "user", roles = {"USER"})
    @Sql(
            scripts = "classpath:database/all/books/add-test-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/all/books/delete-test-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void getAll_existingBook_returnsBookDto() throws Exception {
        //When
        MvcResult result = mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        //Then
        String content = result.getResponse().getContentAsString();
        Assertions.assertNotNull(content);
        Assertions.assertFalse(content.isEmpty());
    }

    @Test
    @DisplayName("Create Book")
    @Sql(
            scripts = "classpath:database/all/books/delete-test-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveBook_validRequestDto_returnsBookDto() throws Exception {
        //Given
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle(BOOK_TITLE)
                .setAuthor(BOOK_AUTHOR)
                .setIsbn(ISBN)
                .setPrice(PRICE);

        BookDto expected = new BookDto()
                .setTitle(requestDto.getTitle())
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn())
                .setPrice(requestDto.getPrice());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        //Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getAuthor(), actual.getAuthor());
        assertEquals(expected.getIsbn(), actual.getIsbn());
        assertEquals(0, expected.getPrice().compareTo(actual.getPrice()));
    }

    @Test
    @DisplayName("Find book by id")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = "classpath:database/all/books/add-test-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/all/books/delete-test-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void findById_existingId_returnsBookDto() throws Exception {
        //When
        MvcResult result = mockMvc.perform(get("/books/{id}", BOOK_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class);

        Assertions.assertNotNull(actual);
        assertEquals(BOOK_ID, actual.getId());
        assertEquals(BOOK_TITLE, actual.getTitle());
        assertEquals(BOOK_AUTHOR, actual.getAuthor());
        assertEquals(ISBN, actual.getIsbn());
        assertEquals(PRICE, actual.getPrice());
    }

    @Test
    @DisplayName("Update book")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = "classpath:database/all/books/add-test-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/all/books/delete-test-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void updateBook_validRequestDto_returnsUpdatedBook() throws Exception {
        //Given
        UpdateBookRequestDto updateBookRequestDto = new UpdateBookRequestDto()
                .setTitle(BOOK_TITLE)
                .setAuthor(BOOK_AUTHOR)
                .setIsbn(ISBN)
                .setPrice(PRICE);

        BookDto expected = new BookDto()
                .setTitle(updateBookRequestDto.getTitle())
                .setAuthor(updateBookRequestDto.getAuthor())
                .setIsbn(updateBookRequestDto.getIsbn())
                .setPrice(updateBookRequestDto.getPrice());

        String jsonRequest = objectMapper.writeValueAsString(updateBookRequestDto);

        //When
        MvcResult result = mockMvc.perform(put("/books/{id}", BOOK_ID)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class);
        Assertions.assertNotNull(actual);
        assertEquals(BOOK_ID, actual.getId());

        Book book = bookRepository.findById(BOOK_ID).orElseThrow();
        assertEquals(BOOK_TITLE, book.getTitle());

        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getAuthor(), actual.getAuthor());
        assertEquals(expected.getIsbn(), actual.getIsbn());
        assertEquals(0, expected.getPrice().compareTo(actual.getPrice()));
    }

    @Test
    @DisplayName("Delete book by id")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = "classpath:database/all/books/add-test-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/all/books/delete-test-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void deleteById_existingId_returnsNoContent() throws Exception {
        //When
        mockMvc.perform(delete("/books/{id}", BOOK_ID))
                .andExpect(status().isNoContent());

        //Then
        Boolean isDeleted = jdbcTemplate.queryForObject(
                "SELECT is_deleted FROM books WHERE id = ?",
                Boolean.class,
                BOOK_ID
        );
        assertTrue(isDeleted);
    }
}
