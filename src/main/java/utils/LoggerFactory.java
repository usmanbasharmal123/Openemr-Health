package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

public class LoggerFactory {

	public static Logger getLogger(Class<?> clazz) {
		String className = clazz.getSimpleName();
		String packageName = clazz.getPackageName();

		ThreadContext.put("className", className);

		if (packageName.contains("tests")) {
			ThreadContext.put("logFolder", "tests");
		} else if (packageName.contains("pages")) {
			ThreadContext.put("logFolder", "pages");
		} else if (packageName.contains("driver")) {
			ThreadContext.put("logFolder", "driver");
		} else if (packageName.contains("utils")) {
			ThreadContext.put("logFolder", "utils");
		} else if (packageName.contains("base")) {
			ThreadContext.put("logFolder", "base");
		} else {
			ThreadContext.put("logFolder", "framework");
		}

		return LogManager.getLogger(clazz);
	}
}
