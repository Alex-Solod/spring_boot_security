package mate.academy.spring_boot_security.service;

import lombok.RequiredArgsConstructor;
import mate.academy.spring_boot_security.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.spring_boot_security.dto.category.CategoryDto;
import mate.academy.spring_boot_security.exception.EntityNotFoundException;
import mate.academy.spring_boot_security.mapper.BookMapper;
import mate.academy.spring_boot_security.mapper.CategoryMapper;
import mate.academy.spring_boot_security.model.Category;
import mate.academy.spring_boot_security.repository.BookRepository;
import mate.academy.spring_boot_security.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements  CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final BookMapper bookMapper;
    private final BookRepository bookRepository;

    @Override
    public Page<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(categoryMapper::toDto);
    }

    @Override
    public CategoryDto getCategoryById(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category with id " + id + " not found"));
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        return categoryMapper.toDto(categoryRepository
                .save(categoryMapper.toModel(categoryDto)));
    }

    @Override
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category with id " + id + " not found"));
        categoryMapper.updateCategoryFromDto(categoryDto, category);

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category with id " + id + " not found"));
        category.setDeleted(true);
        categoryRepository.save(category);
    }

    @Override
    public Page<BookDtoWithoutCategoryIds> getBooksByCategoryId(Long id, Pageable pageable) {
        return bookRepository.findAllByCategories_Id(id, pageable)
                .map(bookMapper::toDtoWithoutCategories);
    }
}
