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

    @PostMapping("/collection/shared")
    public ResponseEntity<?> sharedCollection(@RequestBody CollectionSharedCreateRequestDto dto) {
        try {
            if (dto.getTitle() == null || dto.getTitle() == null) {
                throw new RuntimeException("잘못된 DTO 요청입니다.");
            }
            List<String> memberIdList = dto.getMemberIdList();

            // 공유 컬랙션 생성
            CollectionCreateRequestDto createDto = CollectionCreateRequestDto.builder().build();
            createDto.setTitle(dto.getTitle());
            createDto.setNickname(memberIdList.get(0));
            CollectionResponseDto savedCollection
                    = collectionService.createCollection(createDto);

            // 생성된 컬랙션에 친구 추가
            Collection find = collectionService.findCollection
                    (savedCollection.getCollectionId(),savedCollection.getNickname());
            CollectionSharedDetailResponseDto responseDto = null;
            for (var i = 1; i < memberIdList.size(); i += 1) {
                CollectionAddUserDto CollectionAddUser = CollectionAddUserDto.builder()
                        .collectionId(find.getId())
                        .nickname(memberIdList.get(i))
                responseDto = collectionService.addUserToCollection(find, CollectionAddUser);
            }

            // 복사할 컬랙션 존재하면 해당 컬랙션 아이템 전부 복사
            if (dto.getTargetCollectionId() != null) {
                Collection findTarget = collectionService.findCollection
                        (dto.getTargetCollectionId(),savedCollection.getNickname()); // 1차 캐시
                responseDto = collectionService.copyItemToCollection(findTarget, find);
            }
            return ResponseEntity.ok().body(responseDto);
        }catch (Exception e) {
            e.printStackTrace();
            ResponseErrorDto errorDto = ResponseErrorDto.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(errorDto);
        }
    }

    @GetMapping("/collections/shared/{nickname}")
    public ResponseEntity<?> getCollectionsSharedByMember(@PathVariable("nickname") String nickname) {
        try {
            Member member = memberService.findMember(nickname);
            List<CollectionResponseDto> memberCollection = collectionService.findSharedCollectionsByMember(member);
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