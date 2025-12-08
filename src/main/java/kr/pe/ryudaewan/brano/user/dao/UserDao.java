package kr.pe.ryudaewan.brano.user.dao;

import kr.pe.ryudaewan.brano.user.service.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao {
    User selectUserByUid(long uid);

    List<User> selectUsers();

    int insertUser(User user);

    int updateUser(User user);

    int deleteUser(Long uid);
}
