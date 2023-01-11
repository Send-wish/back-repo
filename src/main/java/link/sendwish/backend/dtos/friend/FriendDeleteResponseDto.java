package link.sendwish.backend.dtos.friend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendDeleteResponseDto {
    private String nickname; // 내 닉네임
    private String friendNickname;
}
