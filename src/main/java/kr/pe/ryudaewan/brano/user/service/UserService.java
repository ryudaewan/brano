package kr.pe.ryudaewan.brano.user.service;

import kr.pe.ryudaewan.brano.user.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Transactional(readOnly = true)
    public User getUser(Long uid) {
        User result = this.userDao.selectUserByUid(uid);

        log.debug("조회 결과 = [{}]", result);

        return result;
    }

    @Transactional(readOnly = true)
    public List<User> findUsers() {
        List<User> result = this.userDao.selectUsers();

        if (null == result) {
            result = Collections.emptyList();
        }

        return result;
    }

    @Transactional
    public User registerUser(User user) {
        user.setCreatedAt(LocalDateTime.now());

        try {
            this.userDao.insertUser(user);
        } catch (DuplicateKeyException dke) {
            throw new DuplicateUserException("다른 사용자가 쓰고 있는 이메일로는 신규 사용자 생성 불가능");
        }

        log.debug("생성한 사용자 정보 = [{}]", user);

        return user;
    }

    @Transactional
    public User modifyUser(User user) {
        Long uid = user.getUid();

        if (null == uid) return null;

        User dbUser = this.userDao.selectUserByUid(uid);

        if (null == dbUser) return null;

        if (null != dbUser.getDeletedAt()) return null;

        user.setUpdatedAt(LocalDateTime.now());
        user.setCreatedAt(dbUser.getCreatedAt());
        int cnt = this.userDao.updateUser(user);

        if (cnt < 1) return null;

        return user;
    }

    public int eraseUser(Long uid) {
        return this.userDao.deleteUser(uid);
    }
}
