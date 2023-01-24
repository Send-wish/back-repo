package link.sendwish.backend.controller;

import link.sendwish.backend.dtos.chat.*;
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

    @MessageMapping("/like")
    public void sendLike(ChatLikeRequestDto dto){
        Long like = 1L;
        ChatLikeResponseDto responseDto = ChatLikeResponseDto.builder()
                .itemId(dto.getItemId())
                .like(like)
                .build();
        this.template.convertAndSend("/sub/like/" + dto.getRoomId(), responseDto);
    }
}
