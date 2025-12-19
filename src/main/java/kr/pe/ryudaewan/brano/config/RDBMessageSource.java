package kr.pe.ryudaewan.brano.config;

import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import kr.pe.ryudaewan.brano.message.dao.MessageDao;
import kr.pe.ryudaewan.brano.message.service.MessageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component("messageSource")
@Slf4j
public class RDBMessageSource extends AbstractMessageSource {
    private final MessageDao messageDao;

    @Autowired
    public RDBMessageSource(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    private final Map<MessageVo, String> messageCache = new HashMap<>();

    @PostConstruct
    public void init() {
        List<MessageVo> msgs = this.messageDao.selectMessages();
        msgs.forEach(msg -> this.messageCache.put(msg, msg.getMessageContent()));
    }

    @Override
    protected MessageFormat resolveCode(@Nonnull String code, Locale locale) {
        MessageVo message = new MessageVo();
        message.setMessageKey(code);
        message.setLocale(locale.getLanguage());
        String messageContent = this.messageCache.get(message);

        if (StringUtils.hasText(messageContent)) {
            return new MessageFormat(messageContent, locale);
        }

        /* 메시지 캐시에 해당 메시지 코드에 해당하는 메시지 없단 뜻 */
        MessageVo dbMessage = messageDao.selectMessageByMessageKeyAndLocale(message);

        if (dbMessage == null) {
            throw new NoSuchMessageException(code);
        } else {
            /* 메시지 캐시에 메시지 추가 */
            this.messageCache.put(message, dbMessage.getMessageContent());
        }

        return new MessageFormat(message.getMessageContent(), locale);
    }
}
