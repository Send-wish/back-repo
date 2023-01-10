package link.sendwish.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberFriendAddResponseDto {
    private Long id; // my id
    private String myNickname; // my nickname
    private String friendNickname; // 친구의 nickname
}
