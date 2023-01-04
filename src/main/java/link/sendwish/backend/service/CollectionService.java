package link.sendwish.backend.service;

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

        Member member = memberRepository.findByMemberId(dto.getMemberId()).orElseThrow(RuntimeException::new);
        MemberCollection memberCollection = MemberCollection.builder()
                .member(member)
                .collection(collection)
                .build();

        /*
        * Collection의 Cascade 옵션으로 인해 MemberCollectionRepository.save() 호출 X
        * */
        collectionRepository.save(collection);

        member.addMemberCollection(memberCollection);
        collection.addMemberCollection(memberCollection);//Cascade Option으로 insert문 자동 호출


        log.info("컬렉션 생성 [ID] : {}, [컬렉션 제목] : {}", member.getMemberId(), collection.getTitle());

        return CollectionResponseDto.builder()
                .memberId(member.getMemberId())
                .title(collection.getTitle())
                .collectionId(collection.getId())
                .build();
    }

    public List<CollectionResponseDto> findCollectionsByMember(Member member) {
        List<CollectionResponseDto> dtos = memberCollectionRepository
                .findAllByMember(member)
                .orElseThrow(RuntimeException::new)
                .stream()
                .map(target -> CollectionResponseDto
                        .builder()
                        .title(target.getCollection().getTitle())
                        .memberId(target.getMember().getUsername())
                        .collectionId(target.getId())
                        .build()
                ).toList();
        log.info("컬렉션 일괄 조회 [ID] : {}, [컬렉션 갯수] : {}", member.getMemberId(), dtos.size());
        return dtos;
    }

    public Collection findCollection(Long collectionId,String memberId) {
        Collection find = collectionRepository.findById(collectionId).orElseThrow(RuntimeException::new);
        log.info("컬렉션 단건 조회 [ID] : {}, [컬렉션 제목] : {}", memberId, find.getTitle());
        return find;
    }

    public CollectionDetailResponseDto getDetails(Collection collection,String memberId) {
        List<Item> items = collection.getCollectionItems()
                .stream().map(CollectionItem::getItem).toList();
        return CollectionDetailResponseDto.builder()
                .collectionId(collection.getId())
                .memberId(memberId)
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
            throw new RuntimeException("수정하려는 제목이 동일합니다.");
        }
        collection.changeTitle(dto.getNewTitle());
        Collection findByCache = collectionRepository
                .findById(dto.getCollectionId()).orElseThrow(RuntimeException::new);
        assert findByCache.getTitle().equals(dto.getNewTitle());
        log.info("컬렉션 제목 수정 [ID] : {}, [수정된 컬렉션 제목] : {}", dto.getMemberId(), findByCache.getTitle());
        return CollectionResponseDto.builder()
                .title(findByCache.getTitle())
                .memberId(dto.getMemberId())
                .build();
    }

}
