package mate.academy.spring_boot_security.mapper;

import mate.academy.spring_boot_security.dto.book.BookDto;
import mate.academy.spring_boot_security.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.spring_boot_security.dto.book.CreateBookRequestDto;
import mate.academy.spring_boot_security.dto.book.UpdateBookRequestDto;
import mate.academy.spring_boot_security.model.Book;
import mate.academy.spring_boot_security.model.Category;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookDto toDto(Book book);

    @Mapping(target = "categories", ignore = true)
    Book toModel(CreateBookRequestDto requestDto);

    @Mapping(target = "categories", ignore = true)
    void updateBookFromDto(UpdateBookRequestDto dto,
                           @MappingTarget Book book);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        if (book.getCategories() != null) {
            Set<Long> collect = book.getCategories().stream()
                    .map(Category::getId)
                    .collect(Collectors.toSet());
            bookDto.setCategoryIds(collect);
        }
    }

    @AfterMapping
    default void setCategories(@MappingTarget Book book, CreateBookRequestDto requestDto) {
        if (requestDto.getCategoryIds() != null) {
            Set<Category> categories = requestDto.getCategoryIds().stream()
                    .map(id -> {
                        Category category = new Category();
                        category.setId(id);
                        return category;
                    })
                    .collect(Collectors.toSet());
            book.setCategories(categories);
        }
    }

    @AfterMapping
    default void setCategories(@MappingTarget Book book, UpdateBookRequestDto requestDto) {
        if (requestDto.getCategoryIds() != null) {
            Set<Category> categories = requestDto.getCategoryIds().stream()
                    .map(id -> {
                        Category category = new Category();
                        category.setId(id);
                        return category;
                    })
                    .collect(Collectors.toSet());
            book.setCategories(categories);
        }
    }
}
