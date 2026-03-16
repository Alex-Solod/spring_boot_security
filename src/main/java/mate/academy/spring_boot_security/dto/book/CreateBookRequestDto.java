package mate.academy.spring_boot_security.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateBookRequestDto {
    @NotNull
    private String title;

    @NotNull
    private String author;

    @NotNull
    private String isbn;

    @NotNull
    @Min(0)
    private BigDecimal price;

    private String description;
    private String coverImage;
}
