package kr.pe.ryudaewan.brano.message.service;

import kr.pe.ryudaewan.brano.base.service.NotExistException;

public class NoMessageException extends NotExistException {
    public NoMessageException() {
        super("message.not.exist");
    }
}
