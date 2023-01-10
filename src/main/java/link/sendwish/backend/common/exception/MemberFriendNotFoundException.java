package link.sendwish.backend.common.exception;

import link.sendwish.backend.common.Messages;

public class MemberFriendNotFoundException extends BusinessException{
    public MemberFriendNotFoundException() {
        super(Messages.NO_FRIEND_USER_MESSAGE);
    }
}
