package link.sendwish.backend.repository;

import link.sendwish.backend.entity.Collection;
import link.sendwish.backend.entity.CollectionItem;
import link.sendwish.backend.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CollectionItemRepository extends JpaRepository<CollectionItem, Long> {
    Optional<CollectionItem> findByCollectionAndItem(Collection collection, Item item);
    void deleteByCollectionAndItem(Collection collection, Item item);
}
