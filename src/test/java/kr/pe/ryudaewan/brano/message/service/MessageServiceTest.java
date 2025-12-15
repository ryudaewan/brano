package kr.pe.ryudaewan.brano.message.service;

import kr.pe.ryudaewan.brano.message.dao.MessageDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    MessageDao messageDao;

    @InjectMocks
    MessageService messageService;

    @Test
    @DisplayName("findMessages: DAO가 null을 반환하면 빈 리스트를 반환한다")
    void findMessages_returnsEmptyList_whenDaoReturnsNull() {
        when(messageDao.selectMessages()).thenReturn(null);

        List<MessageVo> result = messageService.findMessages();

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(messageDao, times(1)).selectMessages();
        verifyNoMoreInteractions(messageDao);
    }

    @Test
    @DisplayName("findMessages: DAO 결과를 그대로 반환한다")
    void findMessages_returnsDaoResult() {
        List<MessageVo> daoResult = Collections.singletonList(new MessageVo());
        when(messageDao.selectMessages()).thenReturn(daoResult);

        List<MessageVo> result = messageService.findMessages();

        assertThat(result).isSameAs(daoResult);
        verify(messageDao, times(1)).selectMessages();
        verifyNoMoreInteractions(messageDao);
    }

    @Test
    @DisplayName("getMessage: DAO 결과를 그대로 반환한다(없으면 null)")
    void getMessage_returnsDaoResult() {
        Long messageId = 10L;
        MessageVo daoMsg = new MessageVo();
        daoMsg.setMessageId(messageId);

        when(messageDao.selectMessageByMessageId(messageId)).thenReturn(daoMsg);

        MessageVo result = messageService.getMessage(messageId);

        assertThat(result).isSameAs(daoMsg);
        verify(messageDao, times(1)).selectMessageByMessageId(messageId);
        verifyNoMoreInteractions(messageDao);
    }

    @Test
    @DisplayName("registerMessage: createdAt을 현재 시각으로 세팅하고 insertMessage를 호출한 뒤 msg를 반환한다")
    void registerMessage_setsCreatedAt_andCallsInsert() {
        MessageVo msg = new MessageVo();
        msg.setLocale("ko");
        msg.setMessageKey("k");
        msg.setMessageContent("v");

        //doNothing().when(messageDao).insertMessage(any(MessageVo.class));
        when(messageDao.insertMessage(any(MessageVo.class))).thenReturn(1);

        LocalDateTime before = LocalDateTime.now();
        MessageVo result = messageService.registerMessage(msg);
        LocalDateTime after = LocalDateTime.now();

        assertThat(result).isSameAs(msg);
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getCreatedAt()).isAfterOrEqualTo(before);
        assertThat(result.getCreatedAt()).isBeforeOrEqualTo(after);

        ArgumentCaptor<MessageVo> captor = ArgumentCaptor.forClass(MessageVo.class);
        verify(messageDao, times(1)).insertMessage(captor.capture());
        assertThat(captor.getValue()).isSameAs(msg);

        verifyNoMoreInteractions(messageDao);
    }

    @Test
    @DisplayName("eraseMessage: DAO deleteMessage 결과를 그대로 반환한다")
    void eraseMessage_returnsDaoDeleteCount() {
        Long messageId = 1L;
        when(messageDao.deleteMessage(messageId)).thenReturn(1);

        int result = messageService.eraseMessage(messageId);

        assertThat(result).isEqualTo(1);
        verify(messageDao, times(1)).deleteMessage(messageId);
        verifyNoMoreInteractions(messageDao);
    }

    @Test
    @DisplayName("modifyMessage: mid가 null이면 null")
    void modifyMessage_returnsNull_whenMidNull() {
        MessageVo msg = new MessageVo();
        msg.setMessageId(1L);

        MessageVo result = messageService.modifyMessage(null, msg);

        assertThat(result).isNull();
        verifyNoInteractions(messageDao);
    }

    @Test
    @DisplayName("modifyMessage: msg가 null이면 null")
    void modifyMessage_returnsNull_whenMsgNull() {
        MessageVo result = messageService.modifyMessage(1L, null);

        assertThat(result).isNull();
        verifyNoInteractions(messageDao);
    }

    @Test
    @DisplayName("modifyMessage: msg.messageId가 null이면 null")
    void modifyMessage_returnsNull_whenMessageIdNull() {
        MessageVo msg = new MessageVo();
        msg.setMessageId(null);

        MessageVo result = messageService.modifyMessage(1L, msg);

        assertThat(result).isNull();
        verifyNoInteractions(messageDao);
    }

    @Test
    @DisplayName("modifyMessage: (현재 구현) mid == msg.messageId 이면 null을 반환한다")
    void modifyMessage_returnsNull_whenMidEqualsMessageId_byCurrentImplementation() {
        MessageVo msg = new MessageVo();
        msg.setMessageId(5L);

        MessageVo result = messageService.modifyMessage(5L, msg);

        assertThat(result).isNull();
        verifyNoInteractions(messageDao);
    }

    @Test
    @DisplayName("modifyMessage: DB에 기존 메시지가 없으면 null")
    void modifyMessage_returnsNull_whenDbMsgNull() {
        MessageVo msg = new MessageVo();
        msg.setMessageId(100L);

        // 현재 구현상 성공 경로로 가려면 mid != msg.messageId 여야 함
        when(messageDao.selectMessageByMessageId(100L)).thenReturn(null);

        MessageVo result = messageService.modifyMessage(999L, msg);

        assertThat(result).isNull();
        verify(messageDao, times(1)).selectMessageByMessageId(100L);
        verifyNoMoreInteractions(messageDao);
    }

    @Test
    @DisplayName("modifyMessage: DB 메시지가 deletedAt != null이면 null")
    void modifyMessage_returnsNull_whenDbMsgDeleted() {
        MessageVo msg = new MessageVo();
        msg.setMessageId(100L);

        MessageVo dbMsg = new MessageVo();
        dbMsg.setMessageId(100L);
        dbMsg.setCreatedAt(LocalDateTime.now().minusDays(1));
        dbMsg.setDeletedAt(LocalDateTime.now().minusHours(1));

        when(messageDao.selectMessageByMessageId(100L)).thenReturn(dbMsg);

        MessageVo result = messageService.modifyMessage(999L, msg);

        assertThat(result).isNull();
        verify(messageDao, times(1)).selectMessageByMessageId(100L);
        verifyNoMoreInteractions(messageDao);
    }

    @Test
    @DisplayName("modifyMessage: updateMessage 결과가 1 미만이면 null")
    void modifyMessage_returnsNull_whenUpdateCountLessThanOne() {
        MessageVo msg = new MessageVo();
        msg.setMessageId(100L);

        MessageVo dbMsg = new MessageVo();
        dbMsg.setMessageId(100L);
        dbMsg.setCreatedAt(LocalDateTime.now().minusDays(1));

        when(messageDao.selectMessageByMessageId(100L)).thenReturn(dbMsg);
        when(messageDao.updateMessage(any(MessageVo.class))).thenReturn(0);

        MessageVo result = messageService.modifyMessage(999L, msg);

        assertThat(result).isNull();
        verify(messageDao, times(1)).selectMessageByMessageId(100L);
        verify(messageDao, times(1)).updateMessage(msg);
        verifyNoMoreInteractions(messageDao);
    }

    @Test
    @DisplayName("modifyMessage: (현재 구현 기준) 조건을 만족하면 updatedAt 세팅, createdAt은 DB값 유지 후 msg 반환")
    void modifyMessage_successPath_byCurrentImplementation() {
        MessageVo msg = new MessageVo();
        msg.setMessageId(100L);
        msg.setLocale("ko");
        msg.setMessageKey("k");
        msg.setMessageContent("v");

        LocalDateTime dbCreatedAt = LocalDateTime.now().minusDays(3);
        MessageVo dbMsg = new MessageVo();
        dbMsg.setMessageId(100L);
        dbMsg.setCreatedAt(dbCreatedAt);
        dbMsg.setDeletedAt(null);

        when(messageDao.selectMessageByMessageId(100L)).thenReturn(dbMsg);
        when(messageDao.updateMessage(any(MessageVo.class))).thenReturn(1);

        LocalDateTime before = LocalDateTime.now();
        MessageVo result = messageService.modifyMessage(999L, msg); // mid != messageId 여야 현재 구현상 진행됨
        LocalDateTime after = LocalDateTime.now();

        assertThat(result).isSameAs(msg);
        assertThat(result.getCreatedAt()).isEqualTo(dbCreatedAt);
        assertThat(result.getUpdatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isAfterOrEqualTo(before);
        assertThat(result.getUpdatedAt()).isBeforeOrEqualTo(after);

        verify(messageDao, times(1)).selectMessageByMessageId(100L);
        verify(messageDao, times(1)).updateMessage(msg);
        verifyNoMoreInteractions(messageDao);
    }
}
