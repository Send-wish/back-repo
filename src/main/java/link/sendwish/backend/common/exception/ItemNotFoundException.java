package link.sendwish.backend.common.exception;

import link.sendwish.backend.common.Messages;

public class ItemNotFoundException extends BusinessException{
    public ItemNotFoundException() {
        super(Messages.NO_ITEM_MESSAGE);
    }
}
