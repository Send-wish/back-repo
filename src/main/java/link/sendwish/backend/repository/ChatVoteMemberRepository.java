package link.sendwish.backend.repository;

import link.sendwish.backend.entity.ChatRoom;
import link.sendwish.backend.entity.ChatVoteMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatVoteMemberRepository extends JpaRepository<ChatVoteMember, Long> {
    Optional<List<ChatVoteMember>> findMemberByChatRoom(ChatRoom chatRoom);
}
