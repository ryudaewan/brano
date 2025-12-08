package kr.pe.ryudaewan.brano.user.dao;

import kr.pe.ryudaewan.brano.user.service.UserVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao {
    UserVo selectUserByUid(long uid);

    List<UserVo> selectUsers();

    int insertUser(UserVo user);

    int updateUser(UserVo user);

    int deleteUser(Long uid);
}
