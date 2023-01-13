package link.sendwish.backend.controller;

import link.sendwish.backend.entity.ChatMessage;
import link.sendwish.backend.entity.ChatRoomMessage;
import link.sendwish.backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageController {
    private final ChatService chatService;
    private final SimpMessageSendingOperations sendingOperations;

    @MessageMapping("/chat/message")
    public void enter(ChatMessage message){
            /* 채팅방 입장 */
            if (ChatMessage.MessageType.ENTER.equals(message.getMessageType())) {
                message.setMessage(message.getSender() + "님이 입장하셨습니다.");
                List<ChatRoomMessage> chatList  = chatService.findAllChatByRoomId(message.getRoomId());
                if(chatList != null){
                    for(ChatRoomMessage chat : chatList ){
                        message.setSender(chat.getChatMessage().getSender());
                        message.setMessage(chat.getChatMessage().getMessage());
                    }
                }
            }
            /* 채팅방에 메세지 전달 */
            sendingOperations.convertAndSend("/topic/chat/room/" + message.getRoomId(), message);
            chatService.saveChatMessage(message);
    }
}
