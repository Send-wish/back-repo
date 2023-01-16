package link.sendwish.backend.controller;

import io.swagger.models.Model;
import link.sendwish.backend.dtos.*;
import link.sendwish.backend.dtos.chat.ChatRoomRequestDto;
import link.sendwish.backend.dtos.chat.ChatRoomResponseDto;
import link.sendwish.backend.entity.Member;
import link.sendwish.backend.service.ChatService;
import link.sendwish.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatService chatService;
    private final MemberService memberService;


    // 모든 채팅방 목록 조회
    @GetMapping("/rooms/{nickname}")
    public ResponseEntity<?> getRoomsByMember(@PathVariable("nickname") String nickname) {
        try{
            Member member = memberService.findMember(nickname);

            List<ChatRoomResponseDto> chatRooms = chatService.findRoomByMember(member);

            return ResponseEntity.ok().body(chatRooms);
        }catch (Exception e) {
            e.printStackTrace();
            ResponseErrorDto errorDto = ResponseErrorDto.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(errorDto);
        }
    }


}
