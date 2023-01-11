package link.sendwish.backend.dtos.friend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendResponseDto {
    private Long friend_id;
    private String friend_nickname;
}
