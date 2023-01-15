package link.sendwish.backend.repository;

import link.sendwish.backend.entity.ChatRoom;
import link.sendwish.backend.entity.ChatRoomMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomMessageRepository extends JpaRepository<ChatRoomMessage, Long> {
    List<ChatRoomMessage> findAllByChatRoom(ChatRoom chatRoom);
}
