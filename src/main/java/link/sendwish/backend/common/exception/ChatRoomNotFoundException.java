package link.sendwish.backend.common.exception;

import link.sendwish.backend.common.Messages;

public class ChatRoomNotFoundException extends BusinessException{
    public ChatRoomNotFoundException() {
        super(Messages.NO_CHAT_ROOM_MESSAGE);
    }
}
