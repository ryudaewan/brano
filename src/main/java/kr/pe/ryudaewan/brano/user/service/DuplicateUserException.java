package kr.pe.ryudaewan.brano.user.service;

import kr.pe.ryudaewan.brano.base.BusinessException;

/**
 * 이미 등록되어 있는 이메일로 신규 사용자 등록 시도 시 발생
 */
public class DuplicateUserException extends BusinessException {
    public DuplicateUserException(String msg) {
        super(msg);
    }
}
