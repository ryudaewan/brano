package kr.pe.ryudaewan.brano.base.service;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ErrorResponseVo {
    private String messageKey;       // 비즈니스 에러 코드
    private String messageContent;    // 사용자 메시지
    private LocalDateTime timestamp; // 발생 시각

    public ErrorResponseVo(String messageKey, String messageContent) {
        this.messageKey = messageKey;
        this.messageContent = messageContent;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponseVo(List<String> error) {
    }
}
