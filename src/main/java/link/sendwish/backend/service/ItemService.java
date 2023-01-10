package link.sendwish.backend.service;


import link.sendwish.backend.dtos.CollectionResponseDto;
import link.sendwish.backend.dtos.ItemResponseDto;
import link.sendwish.backend.entity.*;
import link.sendwish.backend.repository.CollectionItemRepository;
import link.sendwish.backend.repository.MemberItemRepository;
import link.sendwish.backend.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import link.sendwish.backend.common.exception.ItemNotFoundException;

import java.util.List;
import java.util.Optional;

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
    public ItemResponseDto enrollItemToCollection(Collection collection, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
        CollectionItem collectionItem = CollectionItem.builder()
                .item(item)
                .collection(collection)
                .build();

        item.addCollectionItem(collectionItem);
        collection.addCollectionItem(collectionItem);//Cascade Option으로 insert문 자동 호출

        return ItemResponseDto.builder()
                .imgUrl(item.getImgUrl())
                .name(item.getName())
                .price(item.getPrice())
                .originUrl(item.getOriginUrl())
                .itemId(item.getId())
                .build();
    }

    @Transactional
    public void deleteItem(String nickname, Long itemId) {
        Item item = itemRepository
                .findById(itemId)
                .orElseThrow(ItemNotFoundException::new);

        if(item.getReference() == 1){
            itemRepository.delete(item);
            log.info("아이템 삭제 [ID] : {}, [참조 맴버 수] : {}", itemId, 0);
        }else {
            item.subtractReference();
            log.info("아이템 삭제 [ID] : {}, [참조 맴버 수] : {}", itemId, item.getReference());

            Member member = memberService.findMember(nickname);
            List<CollectionResponseDto> memberCollection = collectionService.findCollectionsByMember(member);

            memberCollection.forEach(
                    target -> {
                        Collection collection = collectionService.findCollection(target.getCollectionId(), nickname);
                        CollectionItem collectionItem =
                                collectionItemRepository.findByCollectionAndItem(collection, item).get();
                        collectionItemRepository.deleteByCollectionAndItem(collection, item);
                        item.deleteCollectionItem(collectionItem);
                        collection.deleteCollectionItem(collectionItem);
                    });
        }

    }

    public List<ItemResponseDto> findItemByMember(Member member) {
        List<ItemResponseDto> dtos = memberItemRepository
                .findAllByMember(member)
                .get()
                .stream()
                .map(target -> ItemResponseDto
                        .builder()
                        .name(target.getItem().getName())
                        .originUrl(target.getItem().getOriginUrl())
                        .imgUrl(target.getItem().getImgUrl())
                        .price(target.getItem().getPrice())
                        .itemId(target.getItem().getId())
                        .build()
                ).toList();

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
        log.info("이미 존재하는 아이템 [ID] : {}, [참조하는 맴버 수] : {}", item.getNickname(), item.getReference());
        return item.getId();
    }

}
