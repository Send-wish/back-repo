package link.sendwish.backend.controller;

import link.sendwish.backend.dtos.ItemCreateRequestDto;
import link.sendwish.backend.dtos.ItemEnrollmentRequestDto;
import link.sendwish.backend.dtos.ItemResponseDto;
import link.sendwish.backend.dtos.ResponseErrorDto;
import link.sendwish.backend.entity.Collection;
import link.sendwish.backend.entity.Item;
import link.sendwish.backend.service.CollectionService;
import link.sendwish.backend.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;
    private final CollectionService collectionService;

    //등록된 item id 값 리턴
    @PostMapping("/parsing")
    public ResponseEntity<?> createItem(/*@RequestBody ItemCreateRequestDto dto*/) {
        try {
            /*
            * Python Server 호출, DB에 Item 등록
            * */
            Item item = Item.builder()
                    .name("석유의 종말은 없다")
                    .price(20700)
                    .imgUrl("https://test/12adfasd3")
                    .build();
            Long saveItem = itemService.saveItem(item);

            return ResponseEntity.ok().body(saveItem);
        }catch (Exception e) {
            e.printStackTrace();
            ResponseErrorDto errorDto = ResponseErrorDto.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(errorDto);
        }
    }

    @PostMapping("/enrollment")
    public ResponseEntity<?> enrollItem(@RequestBody ItemEnrollmentRequestDto dto) {
        try {
            /*
            * find Collection 후 , Item 찾아서 (JPA 1차 캐시) 해당 Item을 Collection에 저장
            * 하고 나서 해당 item 상세 정보를 return
            * */
            Collection collection = collectionService.findCollection(dto.getCollectionId(), dto.getMemberId());
            ItemResponseDto itemResponseDto = itemService.enrollItemToCollection(collection, dto.getItemId());
            return ResponseEntity.ok().body(itemResponseDto);
        }catch (Exception e) {
            e.printStackTrace();
            ResponseErrorDto errorDto = ResponseErrorDto.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(errorDto);
        }
    }
}
