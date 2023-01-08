package link.sendwish.backend.controller;

import link.sendwish.backend.common.exception.DtoNullException;
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

    @GetMapping("/collections/{nickname}")
    public ResponseEntity<?> getCollectionsByMember(@PathVariable("nickname") String nickname) {
        try {
            Member member = memberService.findMember(nickname);

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
            if (dto.getNickname() == null || dto.getTitle() == null) {
                throw new DtoNullException();
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
            if (dto.getNewTitle() == null || dto.getNickname() == null || dto.getCollectionId() == null) {
                throw new DtoNullException();
            }
            Collection find = collectionService.findCollection(dto.getCollectionId(),dto.getNickname());
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

    @GetMapping("/collection/{nickname}/{collectionId}")
    public ResponseEntity<?> getDetailCollection(@PathVariable("nickname") String nickname,
                                                 @PathVariable("collectionId") Long collectionId) {
        try {
            Collection collection = collectionService.findCollection(collectionId,nickname);
            CollectionDetailResponseDto dto = collectionService.getDetails(collection, nickname);
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
