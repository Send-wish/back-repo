package link.sendwish.backend.common.exception;

import link.sendwish.backend.common.Messages;

public class MemberChatRoomNotFoundException extends BusinessException{
    public MemberChatRoomNotFoundException() {
        super(Messages.NO_MEMBER_CHAT_ROOM_MESSAGE);
    }
}
