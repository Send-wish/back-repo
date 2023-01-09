package link.sendwish.backend.common.exception;

import link.sendwish.backend.common.Messages;

public class FriendMemberSameException extends BusinessException{
    public FriendMemberSameException() {
        super(Messages.FRIEND_SAME_MEMBER_MESSAGE);
    }
}
