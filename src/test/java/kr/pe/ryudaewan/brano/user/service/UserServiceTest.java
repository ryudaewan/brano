package kr.pe.ryudaewan.brano.user.service;

import kr.pe.ryudaewan.brano.user.dao.UserDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("사용자 조회 - 성공")
    void getUser_success() {
        // given
        Long uid = 1L;
        UserVo mockUser = new UserVo();
        mockUser.setUid(uid);
        mockUser.setEmail("test@test.com");

        given(userDao.selectUserByUid(uid)).willReturn(mockUser);

        // when
        UserVo result = userService.getUser(uid);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUid()).isEqualTo(uid);
        verify(userDao, times(1)).selectUserByUid(uid);
    }

    @Test
    @DisplayName("사용자 목록 조회 - 조회 결과 없음")
    void findUsers_returnEmptyList_whenNull() {
        // given
        given(userDao.selectUsers()).willReturn(null);

        // when
        List<UserVo> result = userService.findUsers();

        // then
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("사용자 등록 - 성공")
    void registerUser_success() {
        // given
        UserVo user = new UserVo();
        user.setEmail("new@test.com");

        given(userDao.insertUser(any(UserVo.class))).willReturn(1);

        // when
        UserVo result = userService.registerUser(user);

        // then
        assertThat(result.getCreatedAt()).isNotNull(); // 생성일시가 세팅되었는지 확인
        verify(userDao, times(1)).insertUser(user);
    }

    @Test
    @DisplayName("사용자 등록 - 기 등록한 이메일 주소로 인해 실패")
    void registerUser_duplicateEmail() {
        // given
        UserVo user = new UserVo();
        user.setEmail("dup@test.com");
        user.setName("Test User");

        given(userDao.insertUser(any(UserVo.class)))
                .willThrow(new DuplicateUserException());

        // when & then
        assertThatThrownBy(() -> userService.registerUser(user))
                .isInstanceOf(DuplicateUserException.class)
                .satisfies(exc -> {
                    DuplicateUserException duex = (DuplicateUserException) exc;
                    assertThat(duex.getErrorCode()).isEqualTo("user.dup.email");
                });
    }

    @Test
    @DisplayName("사용자 수정 - 성공")
    void modifyUser_success() {
        // given
        Long uid = 1L;
        UserVo requestUser = new UserVo();
        requestUser.setUid(uid);
        requestUser.setName("Updated Name");

        UserVo dbUser = new UserVo();
        dbUser.setUid(uid);
        dbUser.setCreatedAt(LocalDateTime.now().minusDays(1)); // 기존 가입일

        given(userDao.selectUserByUid(uid)).willReturn(dbUser);
        given(userDao.updateUser(any(UserVo.class))).willReturn(1);

        // when
        UserVo result = userService.modifyUser(uid, requestUser);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull(); // 수정일시 세팅 확인
        assertThat(result.getCreatedAt()).isEqualTo(dbUser.getCreatedAt()); // 가입일 유지 확인
    }

    @Test
    @DisplayName("사용자 수정 - 없는 사용자는 수정 불가")
    void modifyUser_fail_whenDeleted() {
        // given
        Long uid = 1L;
        UserVo requestUser = new UserVo();
        requestUser.setUid(uid);

        UserVo deletedUser = new UserVo();
        deletedUser.setUid(uid);
        deletedUser.setDeletedAt(LocalDateTime.now()); // 삭제된 상태

        given(userDao.selectUserByUid(uid)).willReturn(deletedUser);

        // when
        UserVo result = userService.modifyUser(1L, requestUser);

        // then
        assertThat(result).isNull();
        verify(userDao, times(0)).updateUser(any());
    }

    @Test
    @DisplayName("사용자 삭제 - 성공")
    void eraseUser_success() {
        // given
        Long uid = 1L;
        given(userDao.deleteUser(uid)).willReturn(1);

        // when
        int result = userService.eraseUser(uid);

        // then
        assertThat(result).isEqualTo(1);
        verify(userDao, times(1)).deleteUser(uid);
    }
}