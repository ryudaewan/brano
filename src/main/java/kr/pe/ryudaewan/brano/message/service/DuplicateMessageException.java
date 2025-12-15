package kr.pe.ryudaewan.brano.message.service;

import kr.pe.ryudaewan.brano.base.service.DuplicateException;

public class DuplicateMessageException extends DuplicateException {
    public DuplicateMessageException() {
        super("message.dup.key");
    }
}
