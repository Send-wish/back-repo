package link.sendwish.backend.common.exception;

import link.sendwish.backend.common.Messages;

public class ScrapingException extends BusinessException{
    public ScrapingException() {
        super(Messages.SCRAPING_ERROR_MESSAGE);
    }
}
