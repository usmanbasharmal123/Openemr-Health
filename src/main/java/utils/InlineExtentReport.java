package utils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class InlineExtentReport {

	private static String loadResource(String path) {
		try (InputStream is = InlineExtentReport.class.getClassLoader().getResourceAsStream(path)) {
			if (is == null) {
				System.out.println("❌ Resource not found in classpath: " + path);
				return "";
			}
			return new String(is.readAllBytes(), StandardCharsets.UTF_8);
		} catch (Exception e) {
			System.out.println("❌ Failed to load resource: " + path + " -> " + e.getMessage());
			return "";
		}
	}

	public static void inlineResources(String reportPath) {
		try {
			String html = Files.readString(Paths.get(reportPath), StandardCharsets.UTF_8);

			// Spark CSS (from ExtentReports JAR)
			String cssSparkStyle = loadResource("com/aventstack/extentreports/view/spark/css/spark-style.css");
			String cssSparkFonts = loadResource("com/aventstack/extentreports/view/spark/css/spark-fonts.css");
			String cssSparkIcons = loadResource("com/aventstack/extentreports/view/spark/css/spark-icons.css");

			// Spark JS (from ExtentReports JAR)
			String jsJsonTree = loadResource("com/aventstack/extentreports/view/spark/js/jsontree.js");
			String jsSparkScript = loadResource("com/aventstack/extentreports/view/spark/js/spark-script.js");

			// Validate core files
			if (cssSparkStyle.isEmpty() || jsJsonTree.isEmpty() || jsSparkScript.isEmpty()) {
				System.out.println("❌ Missing Spark CSS/JS — inline aborted.");
				return;
			}

			// Remove ALL <link> tags
			html = html.replaceAll("(?i)<link[^>]*>", "");

			// Remove ALL <script src="..."> tags
			html = html.replaceAll("(?i)<script[^>]*src=[\"'][^\"']*[\"'][^>]*></script>", "");
			html = html.replaceAll("(?i)<script[^>]*src=[\"'][^\"']*[\"'][^>]*>", "");

			// Inject ALL Spark CSS inline
			String allCss = "<style>" + cssSparkStyle + cssSparkFonts + cssSparkIcons + "</style>";

			html = html.replace("</head>", allCss + "</head>");

			// Inject ALL Spark JS inline
			String allJs = "<script>" + jsJsonTree + jsSparkScript + "</script>";

			int idx = html.lastIndexOf("</body>");
			if (idx != -1) {
				html = html.substring(0, idx) + allJs + html.substring(idx);
			}

			Files.writeString(Paths.get(reportPath), html, StandardCharsets.UTF_8);

			System.out.println("✔ FULL Spark inline completed using CLASSPATH: " + reportPath);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("❌ Failed to inline report.");
		}
	}
}
