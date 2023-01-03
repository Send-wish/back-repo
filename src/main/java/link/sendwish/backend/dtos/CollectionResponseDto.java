package link.sendwish.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionResponseDto {
    private String title;
    private String memberId;
    //추후 item 관련 field 추가
}
