package kr.pe.ryudaewan.brano.message.dao;

import kr.pe.ryudaewan.brano.message.service.MessageVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageDao {
    MessageVo selectMessageByMessageKeyAndLocale(MessageVo msg);
}
