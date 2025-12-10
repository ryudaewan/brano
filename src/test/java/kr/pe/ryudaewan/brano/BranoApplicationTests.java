package kr.pe.ryudaewan.brano;

import kr.pe.ryudaewan.brano.config.RDBMessageSource;
import kr.pe.ryudaewan.brano.config.SecurityConfigLocal;
import kr.pe.ryudaewan.brano.config.ValidationConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import({ValidationConfig.class, SecurityConfigLocal.class, RDBMessageSource.class})
class BranoApplicationTests {

    @Test
    void contextLoads() {
    }

}
