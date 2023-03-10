package link.sendwish.backend.dtos.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponseDto {
    private Long itemId;
    private int price;
    private String name;
    private String imgUrl;
    private String originUrl;
    private String category;
}
