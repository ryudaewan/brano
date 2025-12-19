package kr.pe.ryudaewan.brano.message.service;

import jakarta.validation.constraints.NotBlank;
import kr.pe.ryudaewan.brano.base.service.CommonVo;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) return false;
        MessageVo messageVo = (MessageVo) obj;

        return Objects.equals(locale, messageVo.locale) && Objects.equals(messageKey, messageVo.messageKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locale, messageKey);
    }
}
