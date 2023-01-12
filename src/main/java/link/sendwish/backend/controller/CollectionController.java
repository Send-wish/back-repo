package link.sendwish.backend.controller;

import link.sendwish.backend.common.exception.DtoNullException;
import link.sendwish.backend.common.exception.ItemNotFoundException;
import link.sendwish.backend.dtos.*;
import link.sendwish.backend.dtos.collection.*;
import link.sendwish.backend.dtos.item.ItemDeleteResponseDto;
import link.sendwish.backend.dtos.item.ItemResponseDto;
import link.sendwish.backend.entity.Collection;
import link.sendwish.backend.entity.Item;
import link.sendwish.backend.entity.Member;
import link.sendwish.backend.service.CollectionService;
import link.sendwish.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

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
            List<CollectionResponseDto> reversedMemberCollection = new ArrayList<>(memberCollection);
            Collections.reverse(reversedMemberCollection);

            return ResponseEntity.ok().body(reversedMemberCollection);
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
                        = collectionService.createCollection(dto.getTitle(),dto.getNickname());
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
            CollectionResponseDto responseDto = collectionService.updateCollectionTitle(dto);
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
            CollectionDetailResponseDto dto = collectionService.getDetails(collectionId, nickname);

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
            if (dto.getTitle() == null || dto.getTargetCollectionId() == null) {
                throw new RuntimeException("잘못된 DTO 요청입니다.");
            }

            String owner = dto.getMemberIdList().get(0);

            // 공유 컬랙션 생성
            CollectionResponseDto savedCollection
                    = collectionService.createCollection(dto.getTitle(), owner);

            ArrayList<String> members = new ArrayList<>();
            members.add(owner);

            // 생성된 컬랙션에 친구 추가 => query X
            CollectionResponseDto find = collectionService.findCollection
                    (savedCollection.getCollectionId(), savedCollection.getNickname());
            CollectionSharedDetailResponseDto responseDto = CollectionSharedDetailResponseDto.builder()
                    .title(find.getTitle())
                    .collectionId(find.getCollectionId())
                    .memberIdList(members)
                    .build();

            for (var i = 1; i < dto.getMemberIdList().size(); i += 1) {
                CollectionAddUserResponseDto CollectionAddUserRequest = CollectionAddUserResponseDto.builder()
                        .collectionId(find.getCollectionId())
                        .nickname(dto.getMemberIdList().get(i))
                        .build();
                CollectionAddUserResponseDto collectionAddUser = collectionService.addUserToCollection(find.getCollectionId(), CollectionAddUserRequest);
                responseDto.getMemberIdList().add(collectionAddUser.getNickname());
            }

            // 복사할 컬랙션 존재하면 해당 컬랙션 아이템 전부 복사
            if (dto.getTargetCollectionId() != null) {
                CollectionResponseDto copyTarget = collectionService.findCollection
                        (dto.getTargetCollectionId(), savedCollection.getNickname());// 1차 캐시
                List<ItemResponseDto> copyItemToCollection = collectionService.copyItemToCollection(copyTarget.getCollectionId(),find.getCollectionId());
                responseDto.setDtos(copyItemToCollection);
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
            
    @DeleteMapping("/collection/{nickname}/{collectionId}")
    public ResponseEntity<?> deleteCollection(@PathVariable("nickname") String nickname,
                                              @PathVariable("collectionId") Long collectionId) {
        try {
            CollectionResponseDto dtos = collectionService.deleteCollection(collectionId, nickname);
            return ResponseEntity.ok().body(dtos);
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

    @DeleteMapping("/collection/item")
    public ResponseEntity<?> deleteItem(@RequestBody CollectionItemDeleteRequestDto dto) {
        try{
            if(dto.getItemIdList() == null || dto.getItemIdList().isEmpty() || dto.getCollectionId() == null || dto.getNickname() == null){
                throw new DtoNullException();
            }
            collectionService.deleteCollectionItem(dto.getCollectionId(), dto.getItemIdList());
            return ResponseEntity.ok().body("삭제 성공");
        }catch (Exception e) {
            e.printStackTrace();
            ResponseErrorDto errorDto = ResponseErrorDto.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(errorDto);
        }
    }

}

