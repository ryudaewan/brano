package kr.pe.ryudaewan.brano.message.service;

import jakarta.validation.constraints.NotBlank;
import kr.pe.ryudaewan.brano.base.service.CommonVo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageVo extends CommonVo {
    private Long messageId;
    @NotBlank
    private String locale;
    @NotBlank
    private String messageKey;
    @NotBlank
    private String messageContent;
}
