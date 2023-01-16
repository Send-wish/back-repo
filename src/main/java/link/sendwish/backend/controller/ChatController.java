package link.sendwish.backend.controller;

import link.sendwish.backend.dtos.ResponseErrorDto;
import link.sendwish.backend.service.ChatService;
import link.sendwish.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/chats/{chatRoomId}")
    public ResponseEntity<?> getChatsByChatRoomId(@PathVariable("chatRoomId") Long chatRoomId) {
        try{
            chatService.getChatsByRoom(chatRoomId);
            return ResponseEntity.ok().body(null);
        }catch (Exception e) {
            e.printStackTrace();
            ResponseErrorDto errorDto = ResponseErrorDto.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(errorDto);
        }
    }
    }
