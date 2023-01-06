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
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;
    private final CollectionService collectionService;

    // scrapping-server 연결
    public JSONObject createHttpRequestAndSend(String url) {
        RestTemplate restTemplate = new RestTemplate();

        // Request_body 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("url", url);

        // Request_header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Request_header, Request_body 합친 entity
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        // Post 요청, JSONobject로 응답
        JSONObject jsonObject = new JSONObject(
                restTemplate.postForObject("http://52.79.109.223:5000/webscrap", entity, String.class));

        return jsonObject;
    }


    //등록된 item id 값 리턴
    @PostMapping("/parsing")
    public ResponseEntity<?> createItem(@RequestBody ItemCreateRequestDto dto) {
        try {
            if(dto.getUrl() == null){
                throw new RuntimeException("잘못된 DTO 입니다.");
            }
            /*
            * Python Server 호출, DB에 Item 등록
            * */
            JSONObject jsonObject = createHttpRequestAndSend(dto.getUrl());

            Item item = Item.builder()
                    .name((String)jsonObject.get("title"))
                    .price((Integer)jsonObject.get("price"))
                    .imgUrl((String)jsonObject.get("img"))
                    .originUrl((String)jsonObject.get("url"))
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
            if (dto.getCollectionId() == null || dto.getItemId() == null || dto.getMemberId() == null){
                throw new RuntimeException("잘못된 DTO 입니다.");
            }

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