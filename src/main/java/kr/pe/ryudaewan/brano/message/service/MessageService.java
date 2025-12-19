package kr.pe.ryudaewan.brano.message.service;

import kr.pe.ryudaewan.brano.message.dao.MessageDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MessageService {

    private final MessageDao messageDao;

    @Autowired
    public MessageService(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    @Transactional(readOnly = true)
    public List<MessageVo> findMessages() {
        Optional<List<MessageVo>> result = Optional.ofNullable(this.messageDao.selectMessages());

        return result.orElse(Collections.emptyList());
    }

    @Transactional(readOnly = true)
    public MessageVo getMessage(Long messageId) {
        MessageVo result = this.messageDao.selectMessageByMessageId(messageId);

        log.debug("조회 결과 = [{}]", result);

        return result;
    }

    @Transactional
    public MessageVo registerMessage(MessageVo msg) {
        msg.setCreatedAt(LocalDateTime.now());

        try {
            this.messageDao.insertMessage(msg);
        } catch (DuplicateKeyException dke) {
            throw new DuplicateMessageException();
        }

        log.debug("생성한 메시지 정보 = [{}]", msg);

        return msg;
    }

    @Transactional
    public int eraseMessage(Long messageId) {
        return this.messageDao.deleteMessage(messageId);
    }

    @Transactional
    public MessageVo modifyMessage(Long mid, MessageVo msg) {
        if (null == mid) return null;

        if (null == msg) return null;

        Long messageId = msg.getMessageId();

        if (null == messageId) return null;

        if (mid != msg.getMessageId()) return null;

        MessageVo dbMsg = this.messageDao.selectMessageByMessageId(messageId);

        if (null == dbMsg) return null;

        if (null != dbMsg.getDeletedAt()) return null;

        msg.setUpdatedAt(LocalDateTime.now());
        msg.setCreatedAt(dbMsg.getCreatedAt());

        int cnt = this.messageDao.updateMessage(msg);

        if (cnt < 1) return null;

        return msg;
    }
}
