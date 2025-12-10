package kr.pe.ryudaewan.brano.config;

import jakarta.annotation.Nonnull;
import kr.pe.ryudaewan.brano.message.dao.MessageDao;
import kr.pe.ryudaewan.brano.message.service.MessageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Locale;

@Component("messageSource")
@Slf4j
public class RDBMessageSource extends AbstractMessageSource {
    private final MessageDao messageDao;

    @Autowired
    public RDBMessageSource(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    @Override
    protected MessageFormat resolveCode(@Nonnull String code, Locale locale) {
        MessageVo message = new MessageVo();
        message.setMessageKey(code);
        message.setLocale(locale.getLanguage());
        message = messageDao.selectMessageByMessageKeyAndLocale(message);

        if (message == null) {
            throw new NoSuchMessageException(code);
        }

        return new MessageFormat(message.getMessageContent(), locale);
    }
}
