package link.sendwish.backend.service;



import link.sendwish.backend.common.exception.CollectionNotFoundException;
import link.sendwish.backend.common.exception.MemberNotFoundException;
import link.sendwish.backend.controller.MessageController;
import link.sendwish.backend.dtos.chat.ChatMessageRequestDto;
import link.sendwish.backend.dtos.item.ItemCategoryResponseDto;
import link.sendwish.backend.dtos.item.ItemDeleteResponseDto;
import link.sendwish.backend.dtos.item.ItemResponseDto;
import link.sendwish.backend.dtos.ItemListResponseDto;

import link.sendwish.backend.entity.*;
import link.sendwish.backend.entity.Collection;
import link.sendwish.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import link.sendwish.backend.common.exception.ItemNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final MemberService memberService;
    private final CollectionItemRepository collectionItemRepository;
    private final MemberItemRepository memberItemRepository;
    private final MemberRepository memberRepository;
    private final CollectionRepository collectionRepository;
    private final CollectionService collectionService;
    private final ChatService chatService;
    private final MessageController messageController;

    @Transactional
    public Long saveItem(Item item, String nickname) {
        Item save = itemRepository.save(item);

        Member member = memberService.findMember(nickname);
        MemberItem memberItem = MemberItem.builder().member(member).item(save).build();

        member.addMemberItem(memberItem); //Cascade Option으로 insert문 자동 호출
        item.addMemberItem(memberItem);

        return save.getId();
    }

    @Transactional
    public ItemListResponseDto enrollItemToCollection(String nickname, Long colletionId, List<Long> itemIdList) {
        Collection collection = collectionRepository
                .findById(colletionId).orElseThrow(CollectionNotFoundException::new);
        List<CollectionItem> collectionItemList = collectionItemRepository.findAllByCollection(collection);
        List<Item> itemList = collectionItemList.stream().map(CollectionItem::getItem).collect(Collectors.toList());
        List<Long> itemIdCheckList = itemList.stream().map(Item::getId).collect(Collectors.toList());

        List<Long> itemIdListToSave = new ArrayList<>();
        for (Long itemId : itemIdList){
            if (itemIdCheckList.contains(itemId)){
                log.info("이미 등록된 아이템입니다.");
                continue;
            }
            Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
            CollectionItem collectionItem = CollectionItem.builder()
                    .item(item)
                    .collection(collection)
                    .build();

            item.addCollectionItem(collectionItem);
            collection.addCollectionItem(collectionItem); //Cascade Option으로 insert문 자동 호출
            itemIdListToSave.add(itemId);

            /* 공유 컬랙션에 아이템 추가시 채팅방 메세지 알림 */
            if (collectionService.isSharedCollection(colletionId)) {
                Long chatRoomId = chatService.getChatRoomIdByCollectionId(colletionId);
                ChatMessageRequestDto chat = ChatMessageRequestDto.builder()
                        .sender(nickname)
                        .message(nickname + "님이 아이템을 추가했습니다.")
                        .roomId(chatRoomId)
                        .type(ChatMessage.MessageType.ITEM)
                        .itemId(itemId)
                        .build();
                messageController.sendMessage(chat);
            }
        }

        Collections.reverse(itemIdListToSave);
        log.info("컬랙션 아이템 추가 및 메세지 전송 완료 [ID] : {}, [추가 아이템 수] : {}", collection.getId(), itemIdListToSave.size());

        return ItemListResponseDto.builder()
                .nickname(nickname)
                .collectionId(collection.getId())
                .itemIdList(itemIdListToSave)
                .build();
    }

    @Transactional
    public ItemDeleteResponseDto deleteItem(String nickname, List<Long> listItemId) {

        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(MemberNotFoundException::new);

        for (Long itemId : listItemId){
            Item item = itemRepository
                    .findById(itemId)
                    .orElseThrow(ItemNotFoundException::new);

            if(item.getReference() == 1){
                itemRepository.delete(item);
                log.info("아이템 삭제 [ID] : {}, [참조 맴버 수] : {}", itemId, 0);
            }else{
                item.subtractReference();
                log.info("아이템 삭제 [ID] : {}, [참조 맴버 수] : {}", itemId, item.getReference());
                /*
                 * Cascade Option 적용 X
                 * */
                MemberItem memberItem = memberItemRepository.findByMemberAndItem(member, item)
                        .orElseThrow(RuntimeException::new);
                memberItemRepository.delete(memberItem);

                collectionItemRepository.findAllByItem(item).forEach(collectionItem -> {
                    collectionItemRepository.delete(collectionItem);
                    log.info("컬렉션 아이템 삭제 [ID] : {}", collectionItem.getId());
                });
            }
        }

        return ItemDeleteResponseDto.builder()
                .itemIdList(listItemId)
                .nickname(member.getNickname())
                .build();
    }

    public List<ItemResponseDto> findItemByMember(Member member) {
        List<ItemResponseDto> dtos;
        if (memberItemRepository.findAllByMemberOrderByIdDesc(member).isEmpty()) {
            dtos = new ArrayList<>();
            log.info("맴버 아이템 일괄 조회 [ID] : {},해당 멤버는 가진 아이템이 없습니다.", member.getNickname());
            return dtos;
        }
        dtos = memberItemRepository.findAllByMemberOrderByIdDesc(member)
                .get()
                .stream().map(target -> ItemResponseDto.builder()
                        .originUrl(target.getItem().getOriginUrl())
                        .name(target.getItem().getName())
                        .price(target.getItem().getPrice())
                        .imgUrl(target.getItem().getImgUrl())
                        .itemId(target.getItem().getId())
                        .build()
                ).collect(Collectors.toList());
        log.info("맴버 아이템 일괄 조회 [ID] : {}, [아이템 갯수] : {}", member.getNickname(), dtos.size());
        return dtos;
    }

    public Item findItem(String url) {
        Optional<Item> findByUrl = itemRepository.findByOriginUrl(url);
        return findByUrl.orElse(null);
    }

    @Transactional
    public Long checkMemberReferenceByItem(Item item, String nickname) {
        Member member = memberService.findMember(nickname);
        long find = memberItemRepository
                .findAllByItem(item)
                .get()
                .stream()
                .filter(target -> target
                        .getMember()
                        .getId()
                        .equals(member.getId()))
                        .count();
        if(find == 0){
            item.addReference();
        }
        log.info("이미 존재하는 아이템 [ID] : {}, [참조하는 맴버 수] : {}", item.getId(), item.getReference());
        return item.getId();
    }

    public List<ItemCategoryResponseDto> findCategoryByMemberItem(Member member) {
        List<ItemCategoryResponseDto> dtos = new ArrayList<>();
        Optional<List<MemberItem>> dto = memberItemRepository.findAllByMemberOrderByIdDesc(member);
        if (dto.isEmpty()) {
            log.info("맴버 아이템 일괄 조회 [ID] : {}, 해당 멤버는 가진 아이템이 없습니다.", member.getNickname());
            return dtos;
        }

        Map<String, Long> categoryCount = dto
                .get()
                .stream()
                .collect(Collectors.groupingBy(target -> target.getItem().getCategory(), Collectors.counting()));

        if(categoryCount.containsKey("ETC")){
            categoryCount.remove("ETC");
            if(categoryCount.size() == 0){
                log.info("맴버 [ID] : {}, 해당 멤버는 ETC 에 해당하는 아이템밖에 없습니다.", member.getNickname());
                return dtos;
            }
        }

        Integer total = categoryCount.values().stream().mapToInt(Long::intValue).sum();
        List<String> keySet = new ArrayList<>(categoryCount.keySet());
        keySet.sort((o1, o2) -> categoryCount.get(o2).compareTo(categoryCount.get(o1)));

        for (int i=1; i<= 5; i++){
            if(keySet.size() < i){ break;}
            int finalI = i;
            dtos.add(ItemCategoryResponseDto.builder()
                    .category(keySet.get(i-1))
                    .percentage((int) ((categoryCount.get(keySet.get(i-1)) / (double) total) * 100))
                    .itemDtos(
                            dto
                                    .get()
                                    .stream()
                                    .filter(target -> target.getItem().getCategory().equals(keySet.get(finalI -1)))
                                    .map(target -> ItemResponseDto.builder()
                                            .originUrl(target.getItem().getOriginUrl())
                                            .name(target.getItem().getName())
                                            .price(target.getItem().getPrice())
                                            .imgUrl(target.getItem().getImgUrl())
                                            .itemId(target.getItem().getId())
                                            .category(target.getItem().getCategory())
                                            .build()
                                    ).collect(Collectors.toList())
                    )
                    .build());
        }

        log.info("맴버 아이템 선호도 순위 조회 [ID] : {}", member.getNickname());
        return dtos;
    }


}

