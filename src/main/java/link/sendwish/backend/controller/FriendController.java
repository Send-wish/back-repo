package link.sendwish.backend.controller;


import link.sendwish.backend.common.exception.DtoNullException;
import link.sendwish.backend.dtos.*;
import link.sendwish.backend.dtos.friend.*;
import link.sendwish.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class FriendController {
    private final MemberService memberService;

    @GetMapping("/friend/{nickname}")
    public ResponseEntity<?> getFriendsByMember(@PathVariable("nickname") String nickname){
        try{
            if(nickname == null){
                throw new DtoNullException();
            }
            List<FriendResponseDto> dtos = memberService.findFriendsByMember(nickname);
            return ResponseEntity.ok().body(dtos);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseErrorDto errorDto = ResponseErrorDto.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(errorDto);
        }
    }

    @PostMapping("/friend")
    public ResponseEntity<?> addFriend(@RequestBody FriendAddRequestDto dto) {
        try {
            if (dto.getMemberNickname() == null || dto.getAddMemberNickname() == null) {
                throw new DtoNullException();
            }
            FriendAddResponseDto dtos = memberService.addFriendToMe(dto);
            return ResponseEntity.ok().body(dtos);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseErrorDto errorDto = ResponseErrorDto.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(errorDto);
        }
    }

    @DeleteMapping("/friend")
    public ResponseEntity<?> deleteFriend(@RequestBody FriendDeleteRequestDto dto) {
        try{
            if(dto.getNickname() == null || dto.getFriendNickname() == null){
                throw new DtoNullException();
            }
            FriendDeleteResponseDto dtos = memberService.deleteFriend(dto.getNickname(), dto.getFriendNickname());

            return ResponseEntity.ok().body(dtos);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseErrorDto errorDto = ResponseErrorDto.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(errorDto);
        }
    }


}
