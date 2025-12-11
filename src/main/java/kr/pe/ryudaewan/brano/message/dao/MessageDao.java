package kr.pe.ryudaewan.brano.message.dao;

import kr.pe.ryudaewan.brano.message.service.MessageVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageDao {
    MessageVo selectMessageByMessageKeyAndLocale(MessageVo msg);

    List<MessageVo> selectMessages();

    MessageVo selectMessageByMessageId(Long messageId);

    int insertMessage(MessageVo msg);

    int updateMessage(MessageVo msg);

    int deleteMessage(Long messageId);
}
