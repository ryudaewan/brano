package kr.pe.ryudaewan.brano.base.service;

public class BusinessException extends RuntimeException {
    private final String errorCode;
    private String[] args = null;

    public BusinessException(String errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public BusinessException(String errorCode, String... args) {
        super();
        this.errorCode = errorCode;
        this.args = args;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String[] getArgs() {
        return args;
    }
}
