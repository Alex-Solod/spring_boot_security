package mate.academy.spring_boot_security.dto.book;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateBookRequestDto {
    @NotNull
    private String author;

    @NotNull
    private String title;

    @NotNull
    @PositiveOrZero
    private BigDecimal price;

    private String description;

    private String coverImage;

    @NotNull
    private String isbn;
}
