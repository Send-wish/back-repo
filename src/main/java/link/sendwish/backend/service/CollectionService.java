package link.sendwish.backend.service;

import link.sendwish.backend.repository.CollectionRepository;
import link.sendwish.backend.repository.MemberCollectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final MemberCollectionRepository memberCollectionRepository;




}
