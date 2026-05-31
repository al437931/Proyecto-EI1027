package es.uji.ei1027.sgovi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;

@SpringBootTest
public class CheckConstraintTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void printConstraints() {
        System.out.println("====== START CONSTRAINTS ======");
        try {
            String sql = "SELECT pg_get_constraintdef(c.oid) AS def " +
                         "FROM pg_constraint c " +
                         "JOIN pg_namespace n ON n.oid = c.connamespace " +
                         "WHERE c.conname = 'ri_seleccion_estat'";
            List<String> defs = jdbcTemplate.queryForList(sql, String.class);
            for (String def : defs) {
                System.out.println("CONSTRAINT DEFINITION: " + def);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("====== END CONSTRAINTS ======");
    }
}
