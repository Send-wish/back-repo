package link.sendwish.backend.dtos.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemLikeResponseDto {
    private Long collectionId;
    private Long itemId;
    private String nickname;
    private Boolean isLike;
    private Long likeCount;
}
