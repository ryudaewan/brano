package kr.pe.ryudaewan.brano.base.service;

public class BadInputException extends BusinessException {
    public BadInputException(String errorCode) {
        super(errorCode);
    }

    public BadInputException(String errorCode, String... args) {
        super(errorCode, args);
    }
}
