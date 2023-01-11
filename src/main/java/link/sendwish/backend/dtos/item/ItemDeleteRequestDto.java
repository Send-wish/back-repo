package link.sendwish.backend.dtos.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDeleteRequestDto {
    private String nickname;
    private List<Long> itemIdList;
}
