package link.sendwish.backend.common.exception;

import link.sendwish.backend.common.Messages;

public class MemberNotFoundException extends BusinessException{
    public MemberNotFoundException() {
        super(Messages.NO_USER_MESSAGE);
    }
}
