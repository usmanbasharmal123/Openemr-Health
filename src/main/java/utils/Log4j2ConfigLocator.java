package utils;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

public class Log4j2ConfigLocator {

	public static void printLoadedConfigPath() {
		try {
			LoggerContext context = Configurator.initialize(null, (String) null);
			Configuration config = context.getConfiguration();
			ConfigurationSource source = config.getConfigurationSource();

			String location = source.getLocation();
			if (location != null) {
				System.out.println("🔍 Log4j2 is loading config from: " + location);
			} else {
				System.out.println("⚠️ Log4j2 is using default configuration (no file loaded)");
			}

		} catch (Exception e) {
			System.out.println("❌ Failed to detect Log4j2 config source: " + e.getMessage());
		}
	}
}
