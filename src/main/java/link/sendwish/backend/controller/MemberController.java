package link.sendwish.backend.controller;


import link.sendwish.backend.auth.TokenInfo;
import link.sendwish.backend.common.exception.DtoNullException;
import link.sendwish.backend.dtos.*;
import link.sendwish.backend.dtos.member.MemberProfileResponseDto;
import link.sendwish.backend.dtos.member.MemberRequestDto;
import link.sendwish.backend.dtos.member.MemberResponseDto;
import link.sendwish.backend.dtos.member.SignInResponseDto;
import link.sendwish.backend.entity.Member;
import link.sendwish.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            if (dto.getNickname() == null || dto.getPassword() == null || dto.getNickname() == "" || dto.getPassword() == "") {
                throw new DtoNullException();
            }
            log.info("id = {}", dto.getNickname());
            log.info("pw = {}", dto.getPassword());
            log.info("img = {}", dto.getImg());
            Member member = memberService.createMember(dto);
            MemberResponseDto returnDto = MemberResponseDto.builder()
                    .id(member.getId())
                    .nickname(member.getNickname())
                    .img(member.getImg())
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

    @PostMapping("signin")
    public ResponseEntity<?> signin(@RequestBody MemberRequestDto dto) {
        try {
            if (dto.getNickname() == null || dto.getPassword() == null) {
                throw new DtoNullException();
            }

            TokenInfo tokenInfo = memberService.login(dto.getNickname(), dto.getPassword());
            Member member = memberService.findMember(dto.getNickname());

            SignInResponseDto dtos = SignInResponseDto.builder()
                    .tokenInfo(tokenInfo)
                    .nickname(member.getNickname()).build();

            return ResponseEntity.ok().body(dtos);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseErrorDto errorDto = ResponseErrorDto.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(errorDto);
        }
    }

    // 회원 프로필사진 전송
    @GetMapping("/profile/{nickname}")
    public MemberProfileResponseDto getProfile(@PathVariable String nickname) {
        Member member = memberService.findMember(nickname);
        return MemberProfileResponseDto.builder()
                .nickname(member.getNickname())
                .img(member.getImg())
                .build();
    }
}
