package link.sendwish.backend.controller;

import link.sendwish.backend.dtos.chat.ChatLiveMessageRequestDto;
import link.sendwish.backend.dtos.chat.ChatLiveMessageResponseDto;
import link.sendwish.backend.dtos.chat.ChatMessageRequestDto;
import link.sendwish.backend.dtos.chat.ChatMessageResponseDto;
import link.sendwish.backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {
    private final ChatService chatService;
    private final SimpMessagingTemplate template;

    @MessageMapping("/chat")
    public void sendMessage(ChatMessageRequestDto dto){
        ChatMessageResponseDto responseDto = chatService.saveChatMessage(dto);
        this.template.convertAndSend("/sub/chat/" + responseDto.getChatRoomId(), responseDto);
    }

    @MessageMapping("/live")
    public void sendLiveMessage(ChatLiveMessageRequestDto dto){
        ChatLiveMessageResponseDto responseDto = ChatLiveMessageResponseDto.builder().peerId(dto.getPeerId()).build();
        this.template.convertAndSend("/sub/live/" + dto.getRoomId(), responseDto);
    }
}
