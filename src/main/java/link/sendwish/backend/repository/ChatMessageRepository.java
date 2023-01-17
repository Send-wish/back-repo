package link.sendwish.backend.repository;

import link.sendwish.backend.entity.ChatMessage;
import link.sendwish.backend.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Optional<List<ChatMessage>> findAllByChatRoom(ChatRoom room);
    Optional<ChatMessage> findTopByChatRoomOrderByIdDesc(ChatRoom room);
}
