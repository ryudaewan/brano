package kr.pe.ryudaewan.brano.message.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.pe.ryudaewan.brano.config.RDBMessageSource;
import kr.pe.ryudaewan.brano.config.SecurityConfigLocal;
import kr.pe.ryudaewan.brano.configuration.TestH2Config;
import kr.pe.ryudaewan.brano.message.service.DuplicateMessageException;
import kr.pe.ryudaewan.brano.message.service.MessageService;
import kr.pe.ryudaewan.brano.message.service.MessageVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
@Import({SecurityConfigLocal.class, TestH2Config.class})
@WithMockUser
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @MockitoBean
    private RDBMessageSource messageSource;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("전체 메시지 조회 - 성공")
    void findMessages_Success() throws Exception {
        // given
        Long messageId = 1L;
        String messageKey = "test.message";
        String messageContent = "테스트";
        MessageVo message = new MessageVo();
        message.setMessageId(messageId);
        message.setMessageKey(messageKey);
        message.setMessageContent(messageContent);
        message.setLocale(LocaleContextHolder.getLocale().getLanguage());
        message.setCreatedAt(LocalDateTime.now());

        given(messageService.findMessages()).willReturn(List.of(message));

        // when & then
        mockMvc.perform(get("/messages"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].messageId").value(messageId))
                .andExpect(jsonPath("$[0].messageKey").value(messageKey))
                .andExpect(jsonPath("$[0].messageContent").value(messageContent));
    }

    @Test
    @DisplayName("전체 메시지 조회 - 데이터 없음 (404)")
    void findMessages_NotFound() throws Exception {
        // given
        given(messageService.findMessages()).willReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/messages"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("단일 메시지 조회 - 성공")
    void getMessage_Success() throws Exception {
        // given
        Long messageId = 1L;
        String messageKey = "test.message";
        String messageContent = "테스트";

        MessageVo message = new MessageVo();
        message.setMessageId(messageId);
        message.setMessageKey(messageKey);
        message.setMessageContent(messageContent);
        message.setLocale(LocaleContextHolder.getLocale().getLanguage());
        message.setCreatedAt(LocalDateTime.now());

        given(messageService.getMessage(messageId)).willReturn(message);

        // when & then
        mockMvc.perform(get("/messages/{uid}", messageId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId").value(messageId))
                .andExpect(jsonPath("$.messageKey").value(messageKey))
                .andExpect(jsonPath("$.messageContent").value(messageContent));
    }

    @Test
    @DisplayName("단일 메시지 조회 - 데이터 없음 (404)")
    void getMessage_NotFound() throws Exception {
        // given
        Long uid = 167L;
        given(messageService.getMessage(uid)).willReturn(null);

        // when & then
        mockMvc.perform(get("/messages/{uid}", uid))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("메시지 등록 (POST) - 성공")
    void registerMessage_Success() throws Exception {
        // given
        MessageVo requestMessage = new MessageVo();
        requestMessage.setMessageKey("new.message");
        requestMessage.setMessageContent("New Message");
        requestMessage.setLocale(LocaleContextHolder.getLocale().getLanguage());

        MessageVo savedMessage = new MessageVo();
        savedMessage.setLocale(requestMessage.getLocale());
        savedMessage.setMessageKey(requestMessage.getMessageKey());
        savedMessage.setMessageContent(requestMessage.getMessageContent());
        savedMessage.setMessageId(10L);
        savedMessage.setCreatedAt(LocalDateTime.now());

        given(messageService.registerMessage(any(MessageVo.class))).willReturn(savedMessage);

        // when & then
        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestMessage)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId").value(savedMessage.getMessageId()))
                .andExpect(jsonPath("$.messageKey").value(savedMessage.getMessageKey()))
                .andExpect(jsonPath("$.messageContent").value(savedMessage.getMessageContent()));
    }

    @Test
    @DisplayName("메시지 등록 (POST) - 기 등록한 언어 && 메시지 키로 새 메시지 등록 시도")
    void registerMessage_DupEmail() throws Exception {
        // given
        MessageVo savedMessage = new MessageVo();
        savedMessage.setLocale(LocaleContextHolder.getLocale().getLanguage());
        savedMessage.setMessageKey("message.already.exists");
        savedMessage.setMessageContent("Message Already Exist");
        savedMessage.setMessageId(10L);
        savedMessage.setCreatedAt(LocalDateTime.now());

        MessageVo requestMessage = new MessageVo();
        requestMessage.setMessageKey(savedMessage.getMessageKey());
        requestMessage.setMessageContent(savedMessage.getMessageContent());
        requestMessage.setLocale(savedMessage.getLocale());

        String errorCode = "message.dup.key";
        String errorMessage = "이 언어에는 같은 메시지 키로 등록한 메시지가 이미 있습니다";

        given(messageService.registerMessage(any(MessageVo.class))).willThrow(new DuplicateMessageException());
        given(messageSource.getMessage(errorCode, null, LocaleContextHolder.getLocale()))
                .willReturn(errorMessage);

        // when & then
        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestMessage)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.messageKey").value(errorCode))
                .andExpect(jsonPath("$.messageContent").value(errorMessage));
    }

    @Test
    @DisplayName("메시지 수정 (PUT) - 성공")
    void modifyMessage_Success() throws Exception {
        // given
        MessageVo modifyReq = new MessageVo();
        modifyReq.setMessageId(1L);
        modifyReq.setLocale(LocaleContextHolder.getLocale().getLanguage());
        modifyReq.setMessageKey("message.update.success");
        modifyReq.setMessageContent("메시지 수정 성공");
        LocalDateTime now = LocalDateTime.now();
        modifyReq.setCreatedAt(now);

        given(messageService.modifyMessage(any(Long.class), any(MessageVo.class))).willReturn(modifyReq);

        // when & then
        mockMvc.perform(put("/messages/{uid}", modifyReq.getMessageId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modifyReq)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageContent").value("메시지 수정 성공"));
    }

    @Test
    @DisplayName("메시지 수정 (PUT) - 없는 메시지 수정 시도")
    void modifyMessage_NotFound() throws Exception {
        // given
        MessageVo modifyReq = new MessageVo();
        modifyReq.setMessageId(176L);
        modifyReq.setLocale(LocaleContextHolder.getLocale().getLanguage());
        modifyReq.setMessageKey("message.update.success");
        modifyReq.setMessageContent("메시지 수정 성공");
        LocalDateTime now = LocalDateTime.now();
        modifyReq.setCreatedAt(now);

        given(messageService.modifyMessage(167L, modifyReq)).willReturn(null);

        // when & then
        mockMvc.perform(put("/messages/{uid}", modifyReq.getMessageId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modifyReq)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("메시지 삭제 - 성공")
    void deleteMessage_Success() throws Exception {
        // given
        Long uid = 1L;
        given(messageService.eraseMessage(uid)).willReturn(1);

        // when & then
        mockMvc.perform(delete("/messages/{uid}", uid))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("메시지 삭제 - 없는 메시지 삭제")
    void deleteMessage_NotFound() throws Exception {
        // given
        Long uid = 167L;
        given(messageService.eraseMessage(uid)).willReturn(0);

        // when & then
        mockMvc.perform(delete("/messages/{uid}", uid))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("메시지 등록 (POST) - 유효성 검증 실패 (400)")
    void registerMessage_ValidationFail() throws Exception {
        // given: @NotBlank 필드들 중 일부 누락/빈 값
        MessageVo invalidRequest = new MessageVo();
        invalidRequest.setLocale(LocaleContextHolder.getLocale().getLanguage());
        invalidRequest.setMessageKey(""); // NotBlank 위반
        invalidRequest.setMessageContent("내용만 있음");

        // when & then
        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}