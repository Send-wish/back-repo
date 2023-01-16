package link.sendwish.backend.controller;

import link.sendwish.backend.dtos.chat.ChatMessageRequestDto;
import link.sendwish.backend.dtos.chat.ChatMessageResponseDto;
import link.sendwish.backend.entity.ChatMessage;
import link.sendwish.backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {
    private ChatService chatService;
    private SimpMessagingTemplate template;

    @Autowired
    public MessageController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/chat") // 해당 url로 메세지 전송되면 메서드 호출
    public void sendMessage(ChatMessageRequestDto dto){
        ChatMessageResponseDto responseDto = chatService.saveChatMessage(dto);
        this.template.convertAndSend("/sub/chat/" + responseDto.getChatRoomId(), responseDto);
    }
}
