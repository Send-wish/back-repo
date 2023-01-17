package link.sendwish.backend.controller;

import link.sendwish.backend.dtos.ResponseErrorDto;
import link.sendwish.backend.dtos.chat.ChatAllMessageResponseDto;
import link.sendwish.backend.dtos.chat.ChatMessageResponseDto;
import link.sendwish.backend.service.ChatService;
import link.sendwish.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/chats/{chatRoomId}")
    public ResponseEntity<?> getChatsByChatRoomId(@PathVariable("chatRoomId") Long chatRoomId) {
        try{
            List<ChatAllMessageResponseDto> chatsByRoom = chatService.getChatsByRoom(chatRoomId);
            return ResponseEntity.ok().body(chatsByRoom);
        }catch (Exception e) {
            e.printStackTrace();
            ResponseErrorDto errorDto = ResponseErrorDto.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(errorDto);
        }
    }
    }
