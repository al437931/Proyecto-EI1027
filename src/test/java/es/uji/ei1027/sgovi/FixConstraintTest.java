package es.uji.ei1027.sgovi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class FixConstraintTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void alterConstraint() {
        System.out.println("====== START ALTER CONSTRAINT ======");
        try {
            // Drop existing constraint
            jdbcTemplate.execute("ALTER TABLE seleccion DROP CONSTRAINT IF EXISTS ri_seleccion_estat");
            
            // Migrate old data
            jdbcTemplate.execute("UPDATE seleccion SET estat = 'pendent_ambdos' WHERE estat = 'pendent'");
            jdbcTemplate.execute("UPDATE seleccion SET estat = 'Acceptada' WHERE estat = 'acceptat'");
            jdbcTemplate.execute("UPDATE seleccion SET estat = 'Rebutjada' WHERE estat = 'rebutjat'");
            
            // Catch any other weird states
            jdbcTemplate.execute("UPDATE seleccion SET estat = 'pendent_ambdos' WHERE estat NOT IN ('Pendent Assistent', 'Pendent Usuari', 'pendent_ambdos', 'Acceptada', 'Rebutjada')");

            // Add new constraint
            jdbcTemplate.execute("ALTER TABLE seleccion ADD CONSTRAINT ri_seleccion_estat CHECK (estat IN ('Pendent Assistent', 'Pendent Usuari', 'pendent_ambdos', 'Acceptada', 'Rebutjada'))");
            
            System.out.println("CONSTRAINT ALTERED SUCCESSFULLY");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("====== END ALTER CONSTRAINT ======");
    }
}
