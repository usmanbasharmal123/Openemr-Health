package utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DebugExtentReport {

	public static void printHtml(String reportPath) {
		try {
			System.out.println("========== DEBUG: RAW HTML CONTENT ==========");
			System.out.println("File: " + reportPath);
			System.out.println("---------------------------------------------");

			String html = Files.readString(Paths.get(reportPath), StandardCharsets.UTF_8);

			// Print first 5000 chars (to avoid console overflow)
			int max = Math.min(html.length(), 5000);
			System.out.println(html.substring(0, max));

			if (html.length() > max) {
				System.out.println("\n... [HTML truncated for console safety] ...");
			}

			System.out.println("========== END DEBUG HTML ==========");

		} catch (Exception e) {
			System.out.println("❌ Failed to read HTML for debugging: " + e.getMessage());
		}
	}
}
