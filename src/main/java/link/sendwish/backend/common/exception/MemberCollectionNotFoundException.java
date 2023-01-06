package link.sendwish.backend.common.exception;

import link.sendwish.backend.common.Messages;

public class MemberCollectionNotFoundException extends BusinessException{
    public MemberCollectionNotFoundException() {
        super(Messages.NO_MEMBER_COLLECTION_MESSAGE);
    }
}
