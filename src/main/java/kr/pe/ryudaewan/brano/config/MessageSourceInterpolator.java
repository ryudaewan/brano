package kr.pe.ryudaewan.brano.config;

import jakarta.validation.MessageInterpolator;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

public class MessageSourceInterpolator implements MessageInterpolator {
    public static final String SEPARATOR = "|!^#|";
    private final RDBMessageSource messageSource;
    private final MessageInterpolator defaultInterpolator; // 폴백(Fallback) 처리용

    public MessageSourceInterpolator(RDBMessageSource messageSource, MessageInterpolator defaultInterpolator) {
        this.messageSource = messageSource;
        this.defaultInterpolator = defaultInterpolator;
    }

    @Override
    public String interpolate(String messageTemplate, Context context, Locale locale) {
        String message;

        // 1. 템플릿에서 중괄호 제거하여 순수한 메시지 키(code)를 추출합니다.
        // 예: {validation.email.required} -> validation.email.required
        String key = messageTemplate.replaceAll("[\\{\\}\\$]", "");

        // 2. 제약 조건의 속성(예: min, max)을 MessageSource의 포맷팅 인수로 사용합니다.
        // @Size(min=5, max=10)의 경우, 5와 10이 배열로 들어갑니다.
        Object[] args = context.getConstraintDescriptor().getAttributes().values().toArray();

        try {
            // 3. RDB 기반 MessageSource를 사용하여 메시지 조회 시도
            message = messageSource.getMessage(key, args, locale);

        } catch (NoSuchMessageException e) {
            // 4. RDB에서 해당 키를 찾지 못하면, 표준 MessageInterpolator에게 폴백(fallback)합니다.
            // 표준 인터폴레이터는 ValidationMessages.properties 파일을 확인합니다.
            message = defaultInterpolator.interpolate(messageTemplate, context, locale);
        }

        return key + "|!^#|" + message;
    }

    @Override
    public String interpolate(String messageTemplate, Context context) {
        return interpolate(messageTemplate, context, Locale.getDefault());
    }
}