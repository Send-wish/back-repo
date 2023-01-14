package link.sendwish.backend.controller;

import link.sendwish.backend.entity.ChatMessage;
import link.sendwish.backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {
    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat") // 해당 url로 메세지 전송되면 메서드 호출
    public void sendMessage(ChatMessage message, SimpMessageHeaderAccessor accessor){
            System.out.println("message = " + message.getMessage());
            simpMessagingTemplate.convertAndSend("/sub/chat/" + message.getRoomId(), message);
            chatService.saveChatMessage(message);
    }
}
