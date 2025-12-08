package kr.pe.ryudaewan.brano.message.service;

import kr.pe.ryudaewan.brano.base.BusinessException;

public class NoMessageException extends BusinessException {
    public NoMessageException(String msg) {
        super(msg);
    }
}
