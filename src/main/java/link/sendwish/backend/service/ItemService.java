package link.sendwish.backend.service;


import link.sendwish.backend.common.exception.CollectionNotFoundException;
import link.sendwish.backend.common.exception.MemberNotFoundException;
import link.sendwish.backend.dtos.collection.CollectionResponseDto;
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

import static java.util.Collections.emptyList;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final MemberService memberService;
    private final CollectionService collectionService;
    private final CollectionItemRepository collectionItemRepository;
    private final MemberItemRepository memberItemRepository;
    private final MemberRepository memberRepository;
    private final CollectionRepository collectionRepository;

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
        Collection collection = collectionRepository.findById(colletionId).orElseThrow(CollectionNotFoundException::new);
        for (Long itemId : itemIdList){
            Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
            CollectionItem collectionItem = CollectionItem.builder()
                    .item(item)
                    .collection(collection)
                    .build();

            item.addCollectionItem(collectionItem);
            collection.addCollectionItem(collectionItem); //Cascade Option으로 insert문 자동 호출
        }

        List<CollectionItem> reverseCollectionItemList = collectionItemRepository
                .findAllByCollectionOrderByIdDesc(collection);

        List<Item> reverseItemList = reverseCollectionItemList.stream()
                .map(CollectionItem::getItem)
                .filter(item -> itemIdList.contains(item.getId()))
                .collect(Collectors.toList());

        List<Long> reverseItemIdList = reverseItemList.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        return ItemListResponseDto.builder()
                .nickname(nickname)
                .collectionId(collection.getId())
                .itemIdList(reverseItemIdList)
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

            }else {
                item.subtractReference();
                log.info("아이템 삭제 [ID] : {}, [참조 맴버 수] : {}", itemId, item.getReference());

                List<CollectionResponseDto> memberCollection = collectionService.findCollectionsByMember(member);

                memberCollection.forEach(
                        target -> {
                            Collection collection = collectionRepository.findById(target.getCollectionId())
                                    .orElseThrow(CollectionNotFoundException::new);
                            CollectionItem collectionItem =
                                    collectionItemRepository.findByCollectionAndItem(collection, item).get();
                            collectionItemRepository.deleteByCollectionAndItem(collection, item);
                            item.deleteCollectionItem(collectionItem);
                            collection.deleteCollectionItem(collectionItem);
                        });
                }
        }

        return(ItemDeleteResponseDto.builder()
                .itemIdList(listItemId)
                .nickname(member.getNickname())
                .build());
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

}
