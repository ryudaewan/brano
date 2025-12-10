package kr.pe.ryudaewan.brano.config;

import jakarta.validation.MessageInterpolator;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationConfig {
    private final RDBMessageSource messageSource; // 1단계에서 구현한 RDB MessageSource

    @Autowired
    public ValidationConfig(RDBMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Bean
    public Validator validator() {

        // 1. 기본 인터폴레이터 획득 (순수 Jakarta Validation API 사용)
        // 이 메서드는 구현체(예: Hibernate Validator)에 의존하지 않고,
        // ValidationMessages.properties 파일을 처리하는 기본 인터폴레이터를 가져옵니다.
        MessageInterpolator defaultInterpolator = Validation.byDefaultProvider()
                .configure()
                .getDefaultMessageInterpolator();

        // 2. 사용자 정의 인터폴레이터 생성 (RDB 연동 로직 적용)
        MessageSourceInterpolator customInterpolator = new MessageSourceInterpolator(
                messageSource,
                defaultInterpolator
        );

        // 3. ValidatorFactory를 빌드하고 커스텀 인터폴레이터를 등록합니다.
        ValidatorFactory factory = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(customInterpolator) // ★ RDB 메시지 소스를 사용하도록 설정
                .buildValidatorFactory();

        return factory.getValidator();
    }
}