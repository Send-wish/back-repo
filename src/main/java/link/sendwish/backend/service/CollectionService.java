package link.sendwish.backend.service;

import link.sendwish.backend.common.exception.CollectionSameTitleException;
import link.sendwish.backend.common.exception.MemberNotFoundException;
import link.sendwish.backend.common.exception.CollectionNotFoundException;
import link.sendwish.backend.common.exception.MemberCollectionNotFoundException;
import link.sendwish.backend.dtos.*;
import link.sendwish.backend.entity.*;
import link.sendwish.backend.repository.CollectionRepository;
import link.sendwish.backend.repository.MemberCollectionRepository;
import link.sendwish.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final MemberRepository memberRepository;
    private final MemberCollectionRepository memberCollectionRepository;


    @Transactional
    public CollectionResponseDto createCollection(CollectionCreateRequestDto dto) {
        Collection collection = Collection.builder()
                .title(dto.getTitle())
                .memberCollections(new ArrayList<>())
                .build();

        Member member = memberRepository.findByNickname(dto.getNickname()).orElseThrow(MemberNotFoundException::new);
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
                .build();
    }

    public List<CollectionResponseDto> findCollectionsByMember(Member member) {
        List<CollectionResponseDto> dtos = memberCollectionRepository
                .findAllByMember(member)
                .orElseThrow(MemberCollectionNotFoundException::new)
                .stream()
                .filter(collection -> collection.getCollection().getReference() == 1)
                .map(target -> CollectionResponseDto
                        .builder()
                        .title(target.getCollection().getTitle())
                        .nickname(target.getMember().getUsername())
                        .collectionId(target.getCollection().getId())
                        .build()
                ).toList();

        log.info("컬렉션 일괄 조회 [ID] : {}, [컬렉션 갯수] : {}", member.getNickname(), dtos.size());
        return dtos;
    }

    public Collection findCollection(Long collectionId,String nickname) {
        Collection find = collectionRepository.findById(collectionId).orElseThrow(CollectionNotFoundException::new);
        log.info("컬렉션 단건 조회 [ID] : {}, [컬렉션 제목] : {}", nickname, find.getTitle());
        return find;
    }

    public CollectionDetailResponseDto getDetails(Collection collection,String nickname) {
        List<Item> items = collection.getCollectionItems()
                .stream().map(CollectionItem::getItem).toList();
        return CollectionDetailResponseDto.builder()
                .collectionId(collection.getId())
                .nickname(nickname)
                .dtos(items.stream().map(
                        target -> ItemResponseDto.builder()
                                .price(target.getPrice())
                                .name(target.getName())
                                .imgUrl(target.getImgUrl())
                                .build()
                ).toList()).build();
    }

    @Transactional
    public CollectionResponseDto updateCollectionTitle(Collection collection,CollectionUpdateRequestDto dto) {
        assert Objects.equals(collection.getId(), dto.getCollectionId());
        if (collection.getTitle().equals(dto.getNewTitle())) {
            throw new CollectionSameTitleException();
        }
        collection.changeTitle(dto.getNewTitle());
        Collection findByCache = collectionRepository
                .findById(dto.getCollectionId()).orElseThrow(CollectionNotFoundException::new);
        assert findByCache.getTitle().equals(dto.getNewTitle());
        log.info("컬렉션 제목 수정 [ID] : {}, [수정된 컬렉션 제목] : {}", dto.getNickname(), findByCache.getTitle());
        return CollectionResponseDto.builder()
                .title(findByCache.getTitle())
                .nickname(dto.getNickname())
                .build();
    }

}
