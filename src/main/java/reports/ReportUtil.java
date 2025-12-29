package reports;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class ReportUtil {

	public static String getLatestReportPath(String folderPath) {
		File folder = new File(folderPath);

		if (!folder.exists()) {
			folder.mkdirs();
		}

		File[] files = folder.listFiles((dir, name) -> name.startsWith("ExtentReport_") && name.endsWith(".html"));

		if (files == null || files.length == 0) {
			return null;
		}

		Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
		return files[0].getAbsolutePath();
	}
}
