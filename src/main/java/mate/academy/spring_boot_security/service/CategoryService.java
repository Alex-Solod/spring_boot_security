package mate.academy.spring_boot_security.service;

import mate.academy.spring_boot_security.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.spring_boot_security.dto.category.CategoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Page<CategoryDto> findAll(Pageable pageable);

    CategoryDto getCategoryById(long id);

    CategoryDto save(CategoryDto categoryDto);

    CategoryDto updateCategory(Long id, CategoryDto categoryDto);

    void deleteById(long id);

    Page<BookDtoWithoutCategoryIds> getBooksByCategoryId(Long id, Pageable pageable);
}
