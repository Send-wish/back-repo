package link.sendwish.backend.repository;

import link.sendwish.backend.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findById(Long itemId);
    Optional<Item> findByOriginUrl(String url);
}
