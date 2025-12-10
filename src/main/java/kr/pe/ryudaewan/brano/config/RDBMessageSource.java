package kr.pe.ryudaewan.brano.config;

import kr.pe.ryudaewan.brano.message.dao.MessageDao;
import kr.pe.ryudaewan.brano.message.service.MessageVo;
import kr.pe.ryudaewan.brano.message.service.NoMessageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Locale;

@Component("messageSource")
public class RDBMessageSource extends AbstractMessageSource {
    private final MessageDao messageDao;

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
