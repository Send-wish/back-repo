package link.sendwish.backend.dtos.friend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendAddResponseDto {
    private String myNickname; // my nickname
    private String friendNickname; // 친구의 nickname
}
