package link.sendwish.backend.service;

import link.sendwish.backend.common.exception.*;
import link.sendwish.backend.dtos.collection.*;
import link.sendwish.backend.dtos.item.ItemResponseDto;
import link.sendwish.backend.entity.*;
import link.sendwish.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final MemberCollectionRepository memberCollectionRepository;
    private final CollectionItemRepository collectionItemRepository;


    @Transactional
    public CollectionResponseDto createCollection(String title, String nickname) {
        Collection collection = Collection.builder()
                .title(title)
                .memberCollections(new ArrayList<>())
                .collectionItems(new ArrayList<>())
                .build();

        Member member = memberRepository.findByNickname(nickname).orElseThrow(MemberNotFoundException::new);

        MemberCollection memberCollection = MemberCollection.builder()
                .member(member)
                .collection(collection)
                .build();

        /*
        * Collection의 Cascade 옵션으로 인해 MemberCollectionRepository.save() 호출 X
        * */
        Collection save = collectionRepository.save(collection);

        member.addMemberCollection(memberCollection);
        collection.addMemberCollection(memberCollection);//Cascade Option으로 insert문 자동 호출

        assert collection.getTitle().equals(save.getTitle());
        log.info("컬렉션 생성 [ID] : {}, [컬렉션 제목] : {}", member.getNickname(), save.getTitle());
        return CollectionResponseDto.builder()
                .nickname(member.getNickname())
                .title(collection.getTitle())
                .collectionId(collection.getId())
                .defaultImage(collection.getDefaultImgURL())
                .build();
    }

    public List<CollectionResponseDto> findCollectionsByMember(Member member) {

        List<CollectionResponseDto> dtos = memberCollectionRepository
                .findAllByMemberOrderByIdDesc(member)
                .orElseThrow(MemberCollectionNotFoundException::new)
                .stream()
                .filter(collection -> collection.getCollection().getReference() == 1)
                .map(target -> CollectionResponseDto
                        .builder()
                        .defaultImage(target.getCollection().getDefaultImgURL())
                        .title(target.getCollection().getTitle())
                        .nickname(target.getMember().getUsername())
                        .collectionId(target.getCollection().getId())
                        .build()
                ).toList();

        log.info("컬렉션 일괄 조회 [ID] : {}, [컬렉션 갯수] : {}", member.getNickname(), dtos.size());
        return dtos;
    }

    public CollectionResponseDto findCollection(Long collectionId,String nickname) {
        Collection find = collectionRepository.findById(collectionId).orElseThrow(CollectionNotFoundException::new);
        log.info("컬렉션 단건 조회 [ID] : {}, [컬렉션 제목] : {}", nickname, find.getTitle());
        return CollectionResponseDto.builder()
                .collectionId(find.getId())
                .nickname(nickname)
                .title(find.getTitle())
                .build();
    }

    public CollectionDetailResponseDto getDetails(Long collectionId,String nickname) {
        Collection collection = collectionRepository
                .findById(collectionId)
                .orElseThrow(CollectionNotFoundException::new);
        log.info("컬렉션 단건 조회 [ID] : {}, [컬렉션 제목] : {}", nickname, collection.getTitle());

        List<Item> items = collection.getReverseCollectionItems()
                .stream().map(CollectionItem::getItem).toList();

        return CollectionDetailResponseDto.builder()
                .collectionId(collection.getId())
                .nickname(nickname)
                .dtos(items.stream().map(
                        target -> ItemResponseDto.builder()
                                .originUrl(target.getOriginUrl())
                                .itemId(target.getId())
                                .price(target.getPrice())
                                .name(target.getName())
                                .imgUrl(target.getImgUrl())
                                .originUrl(target.getOriginUrl())
                                .build()
                ).toList()).build();
    }

    @Transactional
    public CollectionResponseDto updateCollectionTitle(CollectionUpdateRequestDto dto) {
        Collection collection = collectionRepository
                .findById(dto.getCollectionId())
                .orElseThrow(CollectionNotFoundException::new);
        if (collection.getTitle().equals(dto.getNewTitle())) {
            throw new CollectionSameTitleException();
        }
        collection.changeTitle(dto.getNewTitle());
        log.info("컬렉션 제목 수정 [ID] : {}, [수정된 컬렉션 제목] : {}", dto.getNickname(), collection.getTitle());
        return CollectionResponseDto.builder()
                .title(collection.getTitle())
                .nickname(dto.getNickname())
                .collectionId(collection.getId())
                .build();
    }

    @Transactional
    public CollectionResponseDto deleteCollection(Long collectionId, String nickname) {

        Member member = memberRepository.findByNickname(nickname)
                                        .orElseThrow(MemberNotFoundException::new);
        Collection collection = collectionRepository.findById(collectionId)
                                                    .orElseThrow(CollectionNotFoundException::new);

        MemberCollection memberCollection = memberCollectionRepository
                                            .findByMemberAndCollection(member, collection)
                                            .orElseThrow(MemberCollectionNotFoundException::new);

        memberCollectionRepository.deleteByMemberAndCollection(member, collection);


        // reference가 1일시 => 컬렉션 삭제 (Cascade 옵션 고려)
        if(collection.getReference() == 1) {
            collectionRepository.delete(collection);
            if (collectionRepository.findById(collectionId).isPresent()) {
                throw new CollectionNotDeleteException();
            }
        }

        // reference가 1이 아닐시
        else {
            collection.subtractReference();
            member.deleteMemberCollection(memberCollection);
            collection.deleteMemberCollection(memberCollection);
            assert(collection.getReference() == memberCollection.getCollection().getReference());
        }

        log.info("컬렉션 멤버에서 삭제 [ID] : {}, [Title] : {}, [nickname] : {}",
                collection.getId(), collection.getTitle(), member.getNickname());

        return CollectionResponseDto.builder()
                .nickname(member.getNickname())
                .collectionId(collection.getId())
                .title(collection.getTitle())
                .build();
    }

    @Transactional
    public CollectionAddUserResponseDto addUserToCollection(Long collectionId, CollectionAddUserResponseDto dto) {
        Collection find = collectionRepository.findById(collectionId).orElseThrow(CollectionNotFoundException::new);
        Member member = memberRepository.findByNickname(dto.getNickname()).orElseThrow(MemberNotFoundException::new);
        find.addReference();
        MemberCollection memberCollection = MemberCollection.builder()
                .member(member)
                .collection(find)
                .build();

        /*
         * Collection의 Cascade 옵션으로 인해 MemberCollectionRepository.save() 호출 X
         * */
        Collection save = collectionRepository.save(find);

        member.addMemberCollection(memberCollection);
        find.addMemberCollection(memberCollection);


        assert find.getId().equals(save.getId());
        log.info("컬렉션에 사용자 추가 [ID] : {}, [컬렉션 제목] : {}", member.getNickname(), save.getTitle());
        return CollectionAddUserResponseDto.builder()
                .collectionId(save.getId())
                .nickname(member.getNickname())
                .build();
    }

    @Transactional
    public List<ItemResponseDto> copyItemToCollection(Long copiedId, Long targetId) {
        Collection copied = collectionRepository.findById(copiedId).orElseThrow(CollectionNotFoundException::new);
        Collection target = collectionRepository.findById(targetId).orElseThrow(CollectionNotFoundException::new);
        List<Item> items = copied.getCollectionItems()
                .stream().map(CollectionItem::getItem).toList();

        items.forEach(item -> {
            CollectionItem collectionItem = CollectionItem.builder()
                    .item(item)
                    .collection(target)
                    .build();
            item.addCollectionItem(collectionItem); //Cascade Option으로 insert문 자동 호출
        });
        Collection findByCache = collectionRepository
                .findById(target.getId()).orElseThrow(CollectionNotFoundException::new);
        log.info("컬렉션 아이템 복사 [복사된 컬랙션 제목] : {}", findByCache.getTitle());
        return items.stream().map(
                        item -> ItemResponseDto.builder()
                                .itemId(item.getId())
                                .originUrl(item.getOriginUrl())
                                .price(item.getPrice())
                                .name(item.getName())
                                .imgUrl(item.getImgUrl())
                                .build()
                ).toList();
    }

    public List<CollectionResponseDto> findSharedCollectionsByMember(Member member) {
        List<CollectionResponseDto> dtos = memberCollectionRepository
                .findAllByMember(member)
                .orElseThrow(MemberCollectionNotFoundException::new)
                .stream()
                .filter(collection -> collection.getCollection().getReference() > 1)
                .map(target -> CollectionResponseDto
                        .builder()
                        .title(target.getCollection().getTitle())
                        .nickname(target.getMember().getUsername())
                        .collectionId(target.getCollection().getId())
                        .build()
                ).toList();

        log.info("공유 컬렉션 일괄 조회 [ID] : {}, [공유 컬렉션 갯수] : {}", member.getNickname(), dtos.size());
        return dtos;
    }

    @Transactional
    public void deleteCollectionItem(Long collectionId, List<Long> itemIdList) {
        Collection collection = collectionRepository
                .findById(collectionId)
                .orElseThrow(CollectionNotFoundException::new);

        for (Long itemId : itemIdList){
            Item item = itemRepository
                    .findById(itemId)
                    .orElseThrow(ItemNotFoundException::new);

            CollectionItem collectionItem = collectionItemRepository
                    .findByCollectionAndItem(collection, item)
                    .orElseThrow(CollectionItemNotFoundException::new);

            collectionItemRepository.delete(collectionItem);
            item.deleteCollectionItem(collectionItem);
            collection.deleteCollectionItem(collectionItem);
        }
        log.info("컬렉션 아이템 일괄 삭제 [컬렉션 ID] : {}, [삭제된 아이템 갯수] : {}", collectionId, itemIdList.size());
    }

    @Transactional
    public CollectionSharedDetailResponseDto getSharedCollectionDetails(Long collectionId) {
        Collection collection = collectionRepository
                .findById(collectionId)
                .orElseThrow(CollectionNotFoundException::new);
        log.info("공유 컬렉션 단건 조회 [컬렉션 제목] : {}", collection.getTitle());

        String title = collection.getTitle();

        List<Item> items = collection.getReverseCollectionItems()
                .stream().map(CollectionItem::getItem).toList();

        List<String> MemberList = memberCollectionRepository.findAllByCollection(collection)
                .orElseThrow(MemberCollectionNotFoundException::new)
                .stream()
                .map(memberCollection -> memberCollection.getMember().getNickname())
                .toList();

        return CollectionSharedDetailResponseDto.builder()
                .title(title)
                .collectionId(collectionId)
                .memberList(MemberList)
                .dtos(items.stream().map(
                        target -> ItemResponseDto.builder()
                                .itemId(target.getId())
                                .price(target.getPrice())
                                .name(target.getName())
                                .imgUrl(target.getImgUrl())
                                .originUrl(target.getOriginUrl())
                                .build()
                ).toList()).build();
    }
}