package kr.pe.ryudaewan.brano.message.service;

import kr.pe.ryudaewan.brano.message.dao.MessageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Locale;

@Component
public class RDBMessageSource extends AbstractMessageSource {
    private MessageDao messageDao;

    @Autowired
    public RDBMessageSource(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        // 우선 locale 우선순위: ko → en (fallback)
        MessageVo message = new MessageVo();
        message.setMessageKey(code);
        message.setLocale(locale.getLanguage());
        message = messageDao.selectMessageByMessageKeyAndLocale(message);

        if (message == null) {
            throw new NoMessageException();
        }

        return new MessageFormat(message.getMessageContent(), locale);
    }
}
