package link.sendwish.backend.service;

import link.sendwish.backend.dtos.CollectionRequestDto;
import link.sendwish.backend.dtos.CollectionResponseDto;
import link.sendwish.backend.entity.Collection;
import link.sendwish.backend.entity.Member;
import link.sendwish.backend.entity.MemberCollection;
import link.sendwish.backend.repository.CollectionRepository;
import link.sendwish.backend.repository.MemberCollectionRepository;
import link.sendwish.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final MemberRepository memberRepository;
    private final MemberCollectionRepository memberCollectionRepository;


    @Transactional
    public CollectionResponseDto createCollection(CollectionRequestDto dto) {
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
        collection.addMemberCollection(memberCollection);


        log.info("컬렉션 생성 [ID] : {}, [컬렉션 제목] : {}", member.getMemberId(), collection.getTitle());

        return CollectionResponseDto.builder()
                .memberId(member.getMemberId())
                .title(collection.getTitle())
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
}
