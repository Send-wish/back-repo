package link.sendwish.backend.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoom {
    private String roomId;
    private String name;

    public static ChatRoom create(String name) {
        ChatRoom room = new ChatRoom();
        room.roomId = UUID.randomUUID().toString();
        room.name = name;
        return room;
    }
}
