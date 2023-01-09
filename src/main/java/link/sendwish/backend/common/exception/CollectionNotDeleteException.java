package link.sendwish.backend.common.exception;

import link.sendwish.backend.common.Messages;

public class CollectionNotDeleteException extends BusinessException{
    public CollectionNotDeleteException() {
        super(Messages.NOT_DELETE_COLLECTION);
    }
}
