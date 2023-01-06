package link.sendwish.backend.common.exception;

import link.sendwish.backend.common.Messages;

public class MemberExisitingIDException extends BusinessException{
    public MemberExisitingIDException() {
        super(Messages.SAME_MEMBER_ID_MESSAGE);
    }
}
