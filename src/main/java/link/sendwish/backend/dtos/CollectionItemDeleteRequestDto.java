package link.sendwish.backend.dtos;

import link.sendwish.backend.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionItemDeleteRequestDto {
    private List<Long> itemIdList;
    private Long collectionId;
    private String nickname;
}
