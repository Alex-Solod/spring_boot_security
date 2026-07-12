package mate.academy.spring_boot_security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.spring_boot_security.dto.category.CategoryDto;
import mate.academy.spring_boot_security.model.Category;
import mate.academy.spring_boot_security.repository.BookRepository;
import mate.academy.spring_boot_security.repository.CategoryRepository;
import mate.academy.spring_boot_security.service.CategoryService;
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
import org.springframework.jdbc.core.JdbcTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {
    private static final Long CATEGORY_ID = 1L;
    private static final String CATEGORY_NAME = "CATEGORY";
    private static final String CATEGORY_DESCRIPTION = "CATEGORY DESCRIPTION";

    protected static MockMvc mockMvc;

    @Autowired
    private CategoryService categoryService;

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
    @DisplayName("Create category")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = "classpath:database/all/categories/delete-test-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void createCategory_validRequestDto_returnsCategoryDto() throws Exception {
        //Given
        CategoryDto requestDto = new CategoryDto()
                .setName(CATEGORY_NAME)
                .setDescription(CATEGORY_DESCRIPTION);

        CategoryDto expected = new CategoryDto()
                .setName(requestDto.getName())
                .setDescription(requestDto.getDescription());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        //Then
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    @Test
    @DisplayName("Find all categories")
    @WithMockUser(username = "user", roles = {"USER"})
    @Sql(
            scripts = "classpath:database/all/categories/add-test-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/all/categories/delete-test-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void getAll_existingCategories_returnsCategoryDto() throws Exception {
        //When
        MvcResult result = mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        //Then
        String content = result.getResponse().getContentAsString();
        Assertions.assertNotNull(content);
        Assertions.assertFalse(content.isEmpty());
    }

    @Test
    @DisplayName("Find category by id")
    @WithMockUser(username = "user", roles = {"USER"})
    @Sql(
            scripts = "classpath:database/all/categories/add-test-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/all/categories/delete-test-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void getCategoryById_existingId_returnsCategoryDto() throws Exception {
        //When
        MvcResult result = mockMvc.perform(get("/categories/" + CATEGORY_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class);
        Assertions.assertNotNull(actual);
        assertEquals(CATEGORY_ID, actual.getId());
        assertEquals(CATEGORY_NAME, actual.getName());
    }

    @Test
    @DisplayName("Update Categories")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = "classpath:database/all/categories/add-test-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/all/categories/delete-test-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void updateCategory_validRequestDto_returnsCategoryDto() throws Exception {
        //Given
        CategoryDto requestDto = new CategoryDto()
                .setName(CATEGORY_NAME)
                .setDescription(CATEGORY_DESCRIPTION);

        CategoryDto expected = new CategoryDto()
                .setName(requestDto.getName())
                .setDescription(requestDto.getDescription());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc.perform(put("/categories/{id}", CATEGORY_ID)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class);
        Assertions.assertNotNull(actual);
        assertEquals(CATEGORY_ID, actual.getId());

        Category category = categoryRepository.findById(CATEGORY_ID).orElseThrow();
        assertEquals(CATEGORY_NAME, category.getName());
        assertEquals(CATEGORY_DESCRIPTION, category.getDescription());

        Assertions.assertNotNull(actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    @Test
    @DisplayName("Delete category by id")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = "classpath:database/all/categories/add-test-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/all/categories/delete-test-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void deleteCategoryById_existingId_returnsNoContent() throws Exception {
        //When
        mockMvc.perform(delete(
                "/categories/{id}", CATEGORY_ID))
                .andExpect(status().isNoContent());

        //Then
        Boolean isDeleted = jdbcTemplate.queryForObject(
                "SELECT is_deleted FROM categories WHERE id = ?",
                Boolean.class,
                CATEGORY_ID
        );
        assertTrue(isDeleted);
    }

    @Test
    @DisplayName("Get Books by category id")
    @WithMockUser(username = "user", roles = {"USER"})

    @Sql(
            scripts = "classpath:database/all/categories/add-test-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/all/books/add-test-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/all/books/delete-test-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/all/categories/delete-test-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void getBooksByCategoryId_existingCategoryId_returnsBookDto() throws Exception {
        //When
        MvcResult result = mockMvc.perform(get("/categories/{id}/books", CATEGORY_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        String content = result.getResponse().getContentAsString();
        Assertions.assertNotNull(content);
        Assertions.assertFalse(content.isEmpty());
    }
}
