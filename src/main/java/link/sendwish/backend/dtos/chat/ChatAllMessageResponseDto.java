package link.sendwish.backend.dtos.chat;

import link.sendwish.backend.dtos.item.ItemResponseDto;
import link.sendwish.backend.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ChatAllMessageResponseDto {
    private Long chatRoomId;
    private String sender;
    private String message;
    private String createAt;
    private ChatMessage.MessageType type;
    private ItemResponseDto item;
}
