package link.sendwish.backend.controller;


import link.sendwish.backend.auth.TokenInfo;
import link.sendwish.backend.common.exception.DtoNullException;
import link.sendwish.backend.dtos.MemberFriendAddRequestDto;
import link.sendwish.backend.dtos.ResponseErrorDto;
import link.sendwish.backend.dtos.MemberRequestDto;
import link.sendwish.backend.dtos.MemberResponseDto;
import link.sendwish.backend.entity.Member;
import link.sendwish.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/")
    private ResponseEntity home() {
        return ResponseEntity.ok().body("홈 테스트");
    }


    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody MemberRequestDto dto) {
        try {
            if (dto.getNickname() == null || dto.getPassword() == null) {
                throw new DtoNullException();
            }
            log.info("id = {}", dto.getNickname());
            log.info("pw = {}", dto.getPassword());
            Member member = memberService.createMember(dto);
            MemberResponseDto returnDto = MemberResponseDto.builder()
                    .id(member.getId())
                    .nickname(member.getNickname()).build();
            return ResponseEntity.ok().body(returnDto);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseErrorDto errorDto = ResponseErrorDto.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(errorDto);
        }
    }

    @PostMapping("signin")
    public ResponseEntity<?> signin(@RequestBody MemberRequestDto dto) {
        try {
            if (dto.getNickname() == null || dto.getPassword() == null) {
                throw new DtoNullException();
            }
            TokenInfo tokenInfo = memberService.login(dto.getNickname(), dto.getPassword());
            return ResponseEntity.ok().body(tokenInfo);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseErrorDto errorDto = ResponseErrorDto.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(errorDto);
        }
    }

    @PostMapping("/add/friend")
    public ResponseEntity<?> addFriend(@RequestBody MemberFriendAddRequestDto dto) {
        try {
            if (dto.getMemberId() == null || dto.getAddMemberId() == null) {
                throw new DtoNullException();
            }
            Member friendMember = memberService.addFriendToMe(dto);
            MemberResponseDto returnDto = MemberResponseDto.builder()
                    .id(friendMember.getId())
                    .nickname(friendMember.getNickname())
                    .build();
            return ResponseEntity.ok().body(returnDto);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseErrorDto errorDto = ResponseErrorDto.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(errorDto);
        }
    }
}
