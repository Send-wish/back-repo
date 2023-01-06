package link.sendwish.backend.common.exception;

import link.sendwish.backend.common.Messages;

public class DtoNullException extends BusinessException{
    public DtoNullException() {
        super(Messages.DTO_NULL_MESSAGE);
    }
}
