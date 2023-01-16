package link.sendwish.backend.common.exception;

import link.sendwish.backend.common.Messages;

public class ChatMessageNotFoundException extends BusinessException {
    public ChatMessageNotFoundException() {
        super(Messages.NOT_SAVED_CHAT_MESSAGE);
    }
}