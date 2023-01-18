package link.sendwish.backend.dtos.chat;

import link.sendwish.backend.dtos.collection.CollectionResponseDto;
import link.sendwish.backend.dtos.friend.FriendResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponseDto {
    private Long chatRoomId;
    private ChatMessageLastResponseDto lastMessage;
    private CollectionResponseDto collection;
    private List<FriendResponseDto> friends;

}
