package link.sendwish.backend.controller;

import link.sendwish.backend.dtos.CollectionResponseDto;
import link.sendwish.backend.dtos.ResponseErrorDto;
import link.sendwish.backend.entity.Member;
import link.sendwish.backend.service.CollectionService;
import link.sendwish.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CollectionController {

    private final MemberService memberService;

    @GetMapping("/collections/{memberId}")
    public ResponseEntity<?> getCollections(@PathVariable("memberId") String memberId) {
        try {
            Member member = memberService.findMember(memberId);
            List<CollectionResponseDto> memberCollection = memberService.findMemberCollection(member);
            return ResponseEntity.ok().body(memberCollection);
        }catch (Exception e) {
            e.printStackTrace();
            ResponseErrorDto errorDto = ResponseErrorDto.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(errorDto);
        }
    }


}
