package kr.pe.ryudaewan.brano.message.service;

import kr.pe.ryudaewan.brano.message.dao.MessageDao;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Locale;

@Component
public class RDBMessageSource extends AbstractMessageSource {
    private MessageDao messageDao;

    public RDBMessageSource() {
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        // 우선 locale 우선순위: ko → en (fallback)
        MessageVo message = messageDao.selectMessage(code, locale.getLanguage());

        if (message == null) {
            throw new NoMessageException("등록한 메시지 없음");
        }

        return new MessageFormat(message.getMessageContent(), locale);
    }
}
