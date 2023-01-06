package link.sendwish.backend.common.exception;

import link.sendwish.backend.common.Messages;

public class PasswordNotSameException extends BusinessException{
    public PasswordNotSameException() {
        super(Messages.PASSWORD_NOT_EQUAL);
    }
}
