package link.sendwish.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionUpdateRequestDto {
    private String memberId;
    private String newTitle;
    private Long collectionId;
}
