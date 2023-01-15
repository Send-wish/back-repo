package link.sendwish.backend.controller;

import link.sendwish.backend.dtos.chat.ChatMessageResponseDto;
import link.sendwish.backend.entity.ChatMessage;
import link.sendwish.backend.entity.ChatRoomMessage;
import link.sendwish.backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {
    private final ChatService chatService;
    private final SimpMessageSendingOperations simpMessagingTemplate;

    @MessageMapping("/chat") // 해당 url로 메세지 전송되면 메서드 호출
    public void sendMessage(ChatMessage dto){
        /* 채팅방 첫 입장 */
        if (ChatMessage.MessageType.ENTER.equals(dto.getType())) {
            dto.setMessage(dto.getSender() + "님이 입장하셨습니다.");
        }
        /* 채팅방 나가기 */
        else if (ChatMessage.MessageType.QUIT.equals(dto.getType())) {
            dto.setMessage(dto.getSender() + "님이 나가셨습니다.");
        }
        ChatMessageResponseDto responseDto = chatService.saveChatMessage(dto);
        simpMessagingTemplate
                .convertAndSend("/sub/chat/" + responseDto.getChatRoomId(), responseDto);
    }
}
