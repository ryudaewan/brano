package kr.pe.ryudaewan.brano.base.service;

public class NotExistException extends BusinessException {
    public NotExistException(String errorCode) {
        super(errorCode);
    }
}
