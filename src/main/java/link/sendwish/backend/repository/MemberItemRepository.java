package link.sendwish.backend.repository;

import link.sendwish.backend.entity.Item;
import link.sendwish.backend.entity.Member;
import link.sendwish.backend.entity.MemberItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberItemRepository extends JpaRepository<MemberItem, Long> {
    Optional<List<MemberItem>> findAllByMemberOrderByIdDesc(Member member);
    Optional<List<MemberItem>> findAllByItem(Item item);
    Optional<MemberItem> findByMemberAndItem(Member member, Item item);
}