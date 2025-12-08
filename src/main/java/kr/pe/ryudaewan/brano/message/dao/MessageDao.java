package kr.pe.ryudaewan.brano.message.dao;

import kr.pe.ryudaewan.brano.message.service.MessageVo;

public interface MessageDao {

    MessageVo selectMessage(String code, String language);
}
