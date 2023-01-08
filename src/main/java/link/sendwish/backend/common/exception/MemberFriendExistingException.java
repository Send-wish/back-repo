package link.sendwish.backend.common.exception;

import link.sendwish.backend.common.Messages;

public class MemberFriendExistingException extends BusinessException{
    public MemberFriendExistingException() {
        super(Messages.SAME_FRIEND_ID_MESSAGE);
    }
}
