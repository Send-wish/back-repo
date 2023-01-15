package link.sendwish.backend.dtos.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponseDto {
    private Long chatRoomId;
    private String sender;
    private String message;
    private LocalDateTime createAt;
}
