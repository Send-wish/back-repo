package link.sendwish.backend.controller;

import link.sendwish.backend.dtos.*;
import link.sendwish.backend.entity.Collection;
import link.sendwish.backend.entity.Member;
import link.sendwish.backend.service.CollectionService;
import link.sendwish.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CollectionController {

    private final MemberService memberService;
    private final CollectionService collectionService;

    @GetMapping("/collections/{memberId}")
    public ResponseEntity<?> getCollectionsByMember(@PathVariable("memberId") String memberId) {
        try {
            Member member = memberService.findMember(memberId);
            List<CollectionResponseDto> memberCollection = collectionService.findCollectionsByMember(member);
            return ResponseEntity.ok().body(memberCollection);
        }catch (Exception e) {
            e.printStackTrace();
            ResponseErrorDto errorDto = ResponseErrorDto.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(errorDto);
        }
    }

    @PostMapping("/collection")
    public ResponseEntity<?> createCollection(@RequestBody CollectionCreateRequestDto dto) {
        try {
            if (dto.getMemberId() == null || dto.getTitle() == null) {
                throw new RuntimeException("잘못된 DTO 요청입니다.");
            }
                CollectionResponseDto savedCollection
                        = collectionService.createCollection(dto);
                return ResponseEntity.ok().body(savedCollection);
        }catch (Exception e) {
            e.printStackTrace();
            ResponseErrorDto errorDto = ResponseErrorDto.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(errorDto);
        }
    }

    @PatchMapping("/collection")
    public ResponseEntity<?> updateCollectionTitle(@RequestBody CollectionUpdateRequestDto dto) {
        try {
            if (dto.getNewTitle() == null || dto.getMemberId() == null || dto.getCollectionId() == null) {
                throw new RuntimeException("잘못된 DTO 요청입니다.");
            }
            Collection find = collectionService.findCollection(dto.getCollectionId(),dto.getMemberId());
            CollectionResponseDto responseDto = collectionService.updateCollectionTitle(find, dto);
            return ResponseEntity.ok().body(responseDto);
        }catch (Exception e) {
            e.printStackTrace();
            ResponseErrorDto errorDto = ResponseErrorDto.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(errorDto);
        }
    }

    @GetMapping("/collection/{memberId}/{collectionId}")
    public ResponseEntity<?> getDetailCollection(@PathVariable("memberId") String memberId,
                                                 @PathVariable("collectionId") Long collectionId) {
        try {
            Collection collection = collectionService.findCollection(collectionId,memberId);
            CollectionDetailResponseDto dto = collectionService.getDetails(collection, memberId);
            return ResponseEntity.ok().body(dto);
        }catch (Exception e) {
            e.printStackTrace();
            ResponseErrorDto errorDto = ResponseErrorDto.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(errorDto);
        }
    }

}
