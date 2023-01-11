package link.sendwish.backend.common.exception;

import link.sendwish.backend.common.Messages;

public class CollectionItemNotFoundException extends BusinessException{
    public CollectionItemNotFoundException() {
        super(Messages.NO_COLLECTION_ITEM_MESSAGE);
    }
}
