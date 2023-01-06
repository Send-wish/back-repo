package link.sendwish.backend.common.exception;

import link.sendwish.backend.common.Messages;

public class CollectionSameTitleException extends BusinessException{
    public CollectionSameTitleException() {
        super(Messages.SAME_COLLECTION_TITLE_MESSAGE);
    }
}
