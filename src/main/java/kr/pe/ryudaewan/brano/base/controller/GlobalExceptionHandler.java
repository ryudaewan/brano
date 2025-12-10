package kr.pe.ryudaewan.brano.base.controller;

import kr.pe.ryudaewan.brano.base.service.BusinessException;
import kr.pe.ryudaewan.brano.base.service.DuplicateException;
import kr.pe.ryudaewan.brano.base.service.ErrorResponseVo;
import kr.pe.ryudaewan.brano.base.service.NotExistException;
import kr.pe.ryudaewan.brano.config.MessageSourceInterpolator;
import kr.pe.ryudaewan.brano.config.RDBMessageSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

@RestControllerAdvice  // 또는 @ControllerAdvice + @ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    private final RDBMessageSource messageSource;
    private final Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    public GlobalExceptionHandler(RDBMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<ErrorResponseVo> handleValidationExceptions(MethodArgumentNotValidException ex) {

        return ex.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    // 1. 인코딩된 메시지 획득 (예: validation.email.required|||이메일은 필수입니다.)
                    String encodedMessage = error.getDefaultMessage();

                    // 2. ★ 핵심: 구분자로 메시지 키와 최종 메시지를 분리
                    StringTokenizer st = new StringTokenizer(encodedMessage, MessageSourceInterpolator.SEPARATOR);
                    String code = st.nextToken();
                    String message = st.nextToken();

                    return new ErrorResponseVo(code, message);
                })
                .collect(Collectors.toList());
    }

    @ExceptionHandler(NotExistException.class)
    public ResponseEntity<ErrorResponseVo> handleNotExistException(NotExistException notExistEx) {
        return this.makeErrorResponse(notExistEx, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorResponseVo> handleDuplicateException(DuplicateException dupEx) {
        return this.makeErrorResponse(dupEx, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseVo> handleBusinessException(BusinessException bizEx) {
        return this.makeErrorResponse(bizEx, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private ResponseEntity<ErrorResponseVo> makeErrorResponse(BusinessException bizEx, HttpStatus status) {
        String message = this.messageSource.getMessage(bizEx.getErrorCode(), bizEx.getArgs(), this.locale);
        ErrorResponseVo body = new ErrorResponseVo(bizEx.getErrorCode(), message);

        return ResponseEntity.status(status).body(body);
    }

    // 선택: 그 외 예외에 대한 fallback
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleException(
//            Exception ex,
//            HttpServletRequest request
//    ) {
//        ErrorResponse body = new ErrorResponse(
//                "INTERNAL_SERVER_ERROR",
//                "알 수 없는 오류가 발생했습니다.",
//                request.getRequestURI()
//        );
//
//        return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(body);
//    }
}
