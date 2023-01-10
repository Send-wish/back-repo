package link.sendwish.backend.controller;


import link.sendwish.backend.auth.TokenInfo;
import link.sendwish.backend.common.exception.DtoNullException;
import link.sendwish.backend.dtos.*;
import link.sendwish.backend.entity.Member;
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
            List<MemberFriendResponseDto> dtos = memberService.findFriendsByMember(nickname);
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
    public ResponseEntity<?> addFriend(@RequestBody MemberFriendAddRequestDto dto) {
        try {
            if (dto.getMemberNickname() == null || dto.getAddMemberNickname() == null) {
                throw new DtoNullException();
            }
            MemberFriendAddResponseDto dtos = memberService.addFriendToMe(dto);
            return ResponseEntity.ok().body(dtos);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseErrorDto errorDto = ResponseErrorDto.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(errorDto);
        }
    }

    @DeleteMapping("/friend/{nickname}/{friendNickname}")
    public ResponseEntity<?> deleteFriend(@PathVariable("nickname") String nickname,
                                          @PathVariable("friendNickname") String friendNickname){
        try{
            if(nickname == null || friendNickname == null){
                throw new DtoNullException();
            }
            memberService.deleteFriend(nickname, friendNickname);
            return ResponseEntity.ok().body("친구 삭제 성공");
        } catch (Exception e) {
            e.printStackTrace();
            ResponseErrorDto errorDto = ResponseErrorDto.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(errorDto);
        }
    }


}
