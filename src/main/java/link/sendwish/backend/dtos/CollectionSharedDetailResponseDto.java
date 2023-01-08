package link.sendwish.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionSharedDetailResponseDto {
    List<ItemResponseDto> dtos;
    Long collectionId;
    String title;
    List<String> memberIdList;
}
