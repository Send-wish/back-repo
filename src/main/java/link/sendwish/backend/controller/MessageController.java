package link.sendwish.backend.controller;

import link.sendwish.backend.dtos.chat.*;
import link.sendwish.backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {
    private final ChatService chatService;
    private final SimpMessagingTemplate template;
    private final ConcurrentHashMap<Long,String> webRtcSessions = new ConcurrentHashMap<>();

    @MessageMapping("/chat")
    public void sendMessage(ChatMessageRequestDto dto){
        ChatMessageResponseDto responseDto = chatService.saveChatMessage(dto);
        this.template.convertAndSend("/sub/chat/" + responseDto.getChatRoomId(), responseDto);
    }

    @MessageMapping("/live")
    public void sendLiveMessage(ChatLiveMessageRequestDto dto){
        log.info("{}님이 Data Signal을 전송합니다.", dto.getNickname());
        ChatLiveMessageResponseDto responseDto =
                ChatLiveMessageResponseDto.builder()
                        .nickname(dto.getNickname())
                        .signal(dto.getSignal())
                        .build();
        this.template.convertAndSend("/sub/live/" + dto.getRoomId(), responseDto);
    }

    @MessageMapping("/live/enter")
    public void sendLiveMessage(LiveEnterRequestDto dto){
        log.info("{} 님이 통화에 참여합니다.", dto.getNickname());
        ChatLiveMessageResponseDto responseDto =
                ChatLiveMessageResponseDto.builder()
                        .nickname(dto.getNickname())
                        .build();
        this.template.convertAndSend("/sub/live/enter" + dto.getRoomId(), responseDto);
    }
    
    @MessageMapping("/like")
    public void sendLike(ChatLikeRequestDto dto){
        Long like = 1L;
        log.info("{} 님이 투표에 참여합니다.", dto.getNickname());
        ChatLikeResponseDto responseDto = ChatLikeResponseDto.builder()
                .itemId(dto.getItemId())
                .like(like)
                .build();
        this.template.convertAndSend("/sub/like/" + dto.getRoomId(), responseDto);
    }
}
