package link.sendwish.backend.common.exception;

import link.sendwish.backend.common.Messages;

public class CollectionNotFoundException extends BusinessException{
    public CollectionNotFoundException() {
        super(Messages.NO_COLLECTION_MESSAGE);
    }
}
