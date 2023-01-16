package link.sendwish.backend.dtos.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionResponseDto {
    private Long collectionId;
    private String title;
    private String nickname;
    private String defaultImage;
    //추후 item 관련 field 추가

}
