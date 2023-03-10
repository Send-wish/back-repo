package link.sendwish.backend.dtos.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemCategoryResponseDto {
    private String imgUrl;
    private String category;
}
