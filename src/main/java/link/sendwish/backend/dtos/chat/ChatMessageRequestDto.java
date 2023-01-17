package link.sendwish.backend.dtos.chat;

import link.sendwish.backend.entity.ChatMessage.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequestDto {
    private Long roomId;
    private String sender;
    private String message;
    private MessageType type;
    private Long itemId;
}
