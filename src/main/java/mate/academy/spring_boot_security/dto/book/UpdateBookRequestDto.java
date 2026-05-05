package mate.academy.spring_boot_security.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Set;

@Data
public class UpdateBookRequestDto {
    @NotBlank
    private String author;

    @NotBlank
    private String title;

    @NotNull
    @PositiveOrZero
    private BigDecimal price;

    private String description;

    private String coverImage;

    @NotBlank
    private String isbn;

    private Set<Long> categoryIds;
}
