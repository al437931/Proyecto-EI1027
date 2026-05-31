package es.uji.ei1027.sgovi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class SgoviApplication {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(SgoviApplication.class, args);
    }

    @PostConstruct
    public void init() {
        try {
            jdbcTemplate.execute("ALTER TABLE usuariovi ADD COLUMN motiu_rebuig VARCHAR;");
        } catch (Exception e) {}
        try {
            jdbcTemplate.execute("ALTER TABLE assistentpersonal ADD COLUMN motiu_rebuig VARCHAR;");
        } catch (Exception e) {}
        try {
            jdbcTemplate.execute("ALTER TABLE assistentpersonal ALTER COLUMN formacio TYPE VARCHAR(200);");
            jdbcTemplate.execute("ALTER TABLE assistentpersonal ALTER COLUMN experiencia TYPE VARCHAR(200);");
            jdbcTemplate.execute("ALTER TABLE assistentpersonal ALTER COLUMN disponibilitat TYPE VARCHAR(200);");
        } catch (Exception e) {}
    }
}
