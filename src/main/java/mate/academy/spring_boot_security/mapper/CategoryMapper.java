package mate.academy.spring_boot_security.mapper;

import mate.academy.spring_boot_security.dto.category.CategoryDto;
import mate.academy.spring_boot_security.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toModel(CategoryDto categoryDto);

    @Mapping(target = "id", ignore = true)
    void updateCategoryFromDto(CategoryDto categoryDto,
                               @MappingTarget Category category);
}
