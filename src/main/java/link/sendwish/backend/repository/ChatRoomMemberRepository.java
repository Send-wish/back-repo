package link.sendwish.backend.repository;

import link.sendwish.backend.entity.ChatRoom;
import link.sendwish.backend.entity.ChatRoomMember;
import link.sendwish.backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    Optional<List<ChatRoomMember>> findAllByMemberOrderByIdDesc(Member member);
    Optional<List<ChatRoomMember>> findMemberByChatRoom(ChatRoom chatRoom);
}
