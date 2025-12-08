package kr.pe.ryudaewan.brano.user.service;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import kr.pe.ryudaewan.brano.base.CommonVo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserVo extends CommonVo {
    private Long uid;

    @Email(message = "{validation.email.invalid}")
    @NotBlank(message = "{validation.email.required}")
    private String email;

    private String password;

    @NotBlank(message = "{validation.name.required}")
    private String name;
//
//    private LocalDateTime createdAt;
//
//    private LocalDateTime updatedAt;
//
//    private LocalDateTime deletedAt;
}
