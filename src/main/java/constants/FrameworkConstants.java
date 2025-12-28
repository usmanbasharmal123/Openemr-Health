package constants;

import java.nio.file.Paths;

public final class FrameworkConstants {

	private FrameworkConstants() {
	}

	private static final String PROJECT_PATH = Paths.get("").toAbsolutePath().toString();

	public static String getProjectPath() {
		return PROJECT_PATH;
	}

	public static String getConfigFilePath() {
		return PROJECT_PATH + "/src/test/resources/config.properties";
	}

	public static String getLog4j2ConfigPath() {
		return PROJECT_PATH + "/src/test/resources/log4j2.xml";
	}

	public static String getLogsFolderPath() {
		return PROJECT_PATH + "/logs";
	}

	public static String getScreenshotsFolderPath() {
		return PROJECT_PATH + "/screenshots";
	}
}
