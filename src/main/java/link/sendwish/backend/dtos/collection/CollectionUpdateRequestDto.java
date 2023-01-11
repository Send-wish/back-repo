package link.sendwish.backend.dtos.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionUpdateRequestDto {
    private String nickname;
    private String newTitle;
    private Long collectionId;
}
