package es.uji.ei1027.sgovi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class AddColumnsTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void addMotiuRebuig() {
        try {
            jdbcTemplate.execute("ALTER TABLE usuariovi ADD COLUMN motiu_rebuig VARCHAR;");
            System.out.println("Columna afegida a usuariovi");
        } catch (Exception e) {
            System.out.println("Error usuariovi: " + e.getMessage());
        }
        
        try {
            jdbcTemplate.execute("ALTER TABLE assistentpersonal ADD COLUMN motiu_rebuig VARCHAR;");
            System.out.println("Columna afegida a assistentpersonal");
        } catch (Exception e) {
            System.out.println("Error assistentpersonal: " + e.getMessage());
        }
    }
}
