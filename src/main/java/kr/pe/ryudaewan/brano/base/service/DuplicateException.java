package kr.pe.ryudaewan.brano.base.service;

public class DuplicateException extends BusinessException {

    public DuplicateException(String errorCode) {
        super(errorCode);
    }
}
