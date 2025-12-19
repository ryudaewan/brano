package kr.pe.ryudaewan.brano.base.service;

public class NoSuchDataException extends BusinessException {
    public NoSuchDataException(String errorCode) {
        super(errorCode);
    }
}
