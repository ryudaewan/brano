package kr.pe.ryudaewan.brano.login.dao;

import kr.pe.ryudaewan.brano.login.service.TokenVo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenDao extends CrudRepository<TokenVo, String> {
    boolean existsById(String token);
    void deleteById(String token);
}
