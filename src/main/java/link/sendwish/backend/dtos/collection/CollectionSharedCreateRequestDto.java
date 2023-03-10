package link.sendwish.backend.dtos.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionSharedCreateRequestDto {
    private List<String> memberIdList;
    private String title;
    private Long targetCollectionId;
}