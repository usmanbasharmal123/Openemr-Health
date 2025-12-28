package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import constants.FrameworkConstants;

public class ConfigReader {

	// Framework-level logger
	private static final Logger frameworkLog = LogManager.getLogger("FrameworkLogger");

	private static Properties properties;

	static {
		properties = new Properties();
		try (FileInputStream fis = new FileInputStream(FrameworkConstants.getConfigFilePath())) {
			properties.load(fis);
			frameworkLog.info("Loaded config.properties from path: {}", FrameworkConstants.getConfigFilePath());
		} catch (IOException e) {
			frameworkLog.error("Failed to load config.properties file", e);
			throw new RuntimeException("Failed to load config.properties file", e);
		}
	}

	public static String get(String key) {
		String value = properties.getProperty(key);
		frameworkLog.debug("Reading config key: {} = {}", key, value);
		return value;
	}
}
