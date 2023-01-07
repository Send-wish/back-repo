package link.sendwish.backend.service;


import link.sendwish.backend.dtos.ItemResponseDto;
import link.sendwish.backend.entity.Collection;
import link.sendwish.backend.entity.CollectionItem;
import link.sendwish.backend.entity.Item;
import link.sendwish.backend.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import link.sendwish.backend.common.exception.ItemNotFoundException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public Long saveItem(Item item) {
        Item save = itemRepository.save(item);
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
                .build();
    }

    @Transactional
    public void deleteItem(Long itemId) {
        Item item = itemRepository
                .findById(itemId)
                .orElseThrow(ItemNotFoundException::new);

        if(item.getReference() == 1){
            itemRepository.delete(item);
            log.info("아이템 삭제 [ID] : {}, [참조 갯수] : {}", itemId, 0);
        }else {
            item.subtractReference();
            log.info("아이템 삭제 [ID] : {}, [참조 갯수] : {}", itemId, item.getReference());
        }
    }

}
