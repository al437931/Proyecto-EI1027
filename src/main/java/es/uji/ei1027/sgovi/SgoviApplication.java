package es.uji.ei1027.sgovi;

import java.util.logging.Logger;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SgoviApplication {

	private static final Logger log =
			Logger.getLogger(SgoviApplication.class.getName());

	public static void main(String[] args) {
		// Auto-configura l'aplicació
		new SpringApplicationBuilder(SgoviApplication.class).run(args);
	}
}

