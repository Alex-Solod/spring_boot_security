package mate.academy.spring_boot_security.service;

import mate.academy.spring_boot_security.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.spring_boot_security.dto.category.CategoryDto;
import mate.academy.spring_boot_security.exception.EntityNotFoundException;
import mate.academy.spring_boot_security.mapper.BookMapper;
import mate.academy.spring_boot_security.mapper.CategoryMapper;
import mate.academy.spring_boot_security.model.Book;
import mate.academy.spring_boot_security.model.Category;
import mate.academy.spring_boot_security.repository.BookRepository;
import mate.academy.spring_boot_security.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void findAll_categoriesExist_returnCategoryDto() {
        // Given
        Category category = new Category();
        CategoryDto categoryDto = new CategoryDto();
        Pageable pageable = Pageable.unpaged();
        Page<Category> categoryPage = new PageImpl<>(List.of(category));

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        // When
        Page<CategoryDto> result = categoryService.findAll(pageable);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals(categoryDto, result.getContent().getFirst());
        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper).toDto(category);
    }

    @Test
    void saveCategory_validRequest_returnCategoryDto() {
        //Given
        CategoryDto categoryDto = new CategoryDto();
        Category category = new Category();

        when(categoryMapper.toModel(categoryDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        //When
        CategoryDto result = categoryService.save(categoryDto);

        //Then
        assertEquals(categoryDto, result);
        verify(categoryMapper).toModel(categoryDto);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toDto(category);
    }

    @Test
    void getCategoryById_validId_returnCategoryDto() {
        //Given
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        //When
        CategoryDto result = categoryService.getCategoryById(categoryId);

        //Then
        assertEquals(categoryDto, result);
        verify(categoryRepository).findById(categoryId);
        verify(categoryMapper).toDto(category);
    }

    @Test
    void getCategoryById_invalidId_throwEntityNotFoundException() {
        // Given
        Long categoryId = 1L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        //Then
        assertThrows(EntityNotFoundException.class, () -> {
            categoryService.getCategoryById(categoryId);
        });
        verify(categoryRepository).findById(categoryId);
        verify(categoryMapper, never()).toDto(any(Category.class));
    }

    @Test
    void deleteById_validId_deleteCategory() {
        //Given
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        //When
        categoryService.deleteById(categoryId);

        //Then
        assertTrue(category.isDeleted());
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).save(category);
    }

    @Test
    void deleteById_invalidId_throwEntityNotFoundException() {
        //Given
        Long categoryId = 1L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        //Then
        assertThrows(EntityNotFoundException.class, () -> {
            categoryService.deleteById(categoryId);
        });
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void getBooksByCategoryId_validCategoryId_returnBookDtoWithoutCategoryId() {
        //Given
        Long categoryId = 1L;
        Book book = new Book();
        BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds = new BookDtoWithoutCategoryIds();
        Pageable pageable = Pageable.unpaged();
        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookRepository.findAllByCategories_Id(categoryId, pageable)).thenReturn(bookPage);
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(bookDtoWithoutCategoryIds);

        //When
        Page<BookDtoWithoutCategoryIds> result = categoryService
                .getBooksByCategoryId(categoryId, pageable);

        //Then
        assertEquals(1, result.getTotalElements());
        assertEquals(bookDtoWithoutCategoryIds, result.getContent().getFirst());
        verify(bookRepository).findAllByCategories_Id(categoryId, pageable);
        verify(bookMapper).toDtoWithoutCategories(book);
    }
}