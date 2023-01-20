package link.sendwish.backend.dtos.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatLiveMessageResponseDto {
    private String peerId;
}
