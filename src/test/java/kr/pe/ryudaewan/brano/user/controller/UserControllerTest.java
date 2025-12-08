package kr.pe.ryudaewan.brano.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.pe.ryudaewan.brano.config.SecurityConfigLocal;
import kr.pe.ryudaewan.brano.configuration.TestH2Config;
import kr.pe.ryudaewan.brano.user.service.UserService;
import kr.pe.ryudaewan.brano.user.service.UserVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
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

@WebMvcTest(UserController.class)
@Import({SecurityConfigLocal.class, TestH2Config.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("전체 사용자 조회 - 성공")
    void findUsers_Success() throws Exception {
        // given
        UserVo user = new UserVo();
        user.setUid(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setCreatedAt(LocalDateTime.now());

        given(userService.findUsers()).willReturn(List.of(user));

        // when & then
        mockMvc.perform(get("/api/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uid").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test User"));
    }

    @Test
    @DisplayName("전체 사용자 조회 - 데이터 없음 (404)")
    void findUsers_NotFound() throws Exception {
        // given
        given(userService.findUsers()).willReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("단일 사용자 조회 - 성공")
    void getUser_Success() throws Exception {
        // given
        Long uid = 1L;
        UserVo user = new UserVo();
        user.setUid(uid);
        user.setName("Test User");
        user.setCreatedAt(LocalDateTime.now());

        given(userService.getUser(uid)).willReturn(user);

        // when & then
        mockMvc.perform(get("/api/user/{uid}", uid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").value(uid));
    }

    @Test
    @DisplayName("단일 사용자 조회 - 데이터 없음 (404)")
    void getUser_NotFound() throws Exception {
        // given
        Long uid = 167L;
        given(userService.getUser(uid)).willReturn(null);

        // when & then
        mockMvc.perform(get("/api/user/{uid}", uid))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("사용자 등록 (PUT) - 성공")
    void registerUser_Success() throws Exception {
        // given
        UserVo requestUser = new UserVo();
        requestUser.setName("New User");
        requestUser.setEmail("new@example.com");

        UserVo savedUser = new UserVo();
        savedUser.setUid(10L);
        savedUser.setName("New User");
        savedUser.setCreatedAt(LocalDateTime.now());

        given(userService.registerUser(any(UserVo.class))).willReturn(savedUser);

        // when & then
        mockMvc.perform(put("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUser)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").value(10L));
    }

//    @Test
//    @DisplayName("사용자 등록 (PUT) - 기 등록한 이메일 주소로 인해 실패")
//    void registerUser_DupEmail() throws Exception {
//        // given
//        User requestUser = new User();
//        requestUser.setName("New User");
//        requestUser.setEmail("new@example.com");
//
//        User savedUser = new User();
//        savedUser.setUid(10L);
//        savedUser.setName("New User");
//        savedUser.setCreatedAt(LocalDateTime.now());
//
//        given(userService.registerUser(any(User.class))).willThrow(new DuplicateUserException("다른 사용자가 쓰고 있는 이메일로는 신규 사용자 생성 불가능"));
//
//        // when & then
//        mockMvc.perform(put("/api/user")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestUser)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.uid").value(10L));
//    }

    @Test
    @DisplayName("사용자 수정 (POST) - 성공")
    void modifyUser_Success() throws Exception {
        // given
        UserVo modifyReq = new UserVo();
        modifyReq.setUid(1L);
        modifyReq.setEmail("updated@brano.com");
        modifyReq.setName("Updated Name");
        LocalDateTime now = LocalDateTime.now();
        modifyReq.setCreatedAt(now);

        given(userService.modifyUser(any(UserVo.class))).willReturn(modifyReq);

        // when & then
        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modifyReq)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    @DisplayName("사용자 수정 (POST) - 없는 사용자 수정 시도")
    void modifyUser_NotFound() throws Exception {
        // given
        UserVo modifyReq = new UserVo();
        modifyReq.setUid(167L);
        modifyReq.setEmail("test@brano.com");
        modifyReq.setName("Updated Name");

        given(userService.modifyUser(modifyReq)).willReturn(null);

        // when & then
        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modifyReq)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("사용자 삭제 - 성공")
    void deleteUser_Success() throws Exception {
        // given
        Long uid = 1L;
        given(userService.eraseUser(uid)).willReturn(1);

        // when & then
        mockMvc.perform(delete("/api/user/{uid}", uid))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("사용자 삭제 - 없는 사용자 삭제")
    void deleteUser_NotFound() throws Exception {
        // given
        Long uid = 167L;
        given(userService.eraseUser(uid)).willReturn(0);

        // when & then
        mockMvc.perform(delete("/api/user/{uid}", uid))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}