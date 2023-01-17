package link.sendwish.backend.dtos.chat;

import link.sendwish.backend.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponseDto {
    private Long chatRoomId;
    private ChatMessage lastMessage;
    private String title;
    private List<String> defaultImage;

}
