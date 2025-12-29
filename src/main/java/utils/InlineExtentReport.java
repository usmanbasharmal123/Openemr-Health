package utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class InlineExtentReport {

	public static void inlineResources(String reportPath, String cssPath, String jsPath) {
		try {
			// Read the generated Extent HTML
			String html = Files.readString(Paths.get(reportPath), StandardCharsets.UTF_8);

			// Read CSS and JS files
			String css = Files.readString(Paths.get(cssPath), StandardCharsets.UTF_8);
			String js = Files.readString(Paths.get(jsPath), StandardCharsets.UTF_8);

			// Inject CSS inside <style> tag
			String inlineCss = "<style>\n" + css + "\n</style>\n</head>";
			html = html.replace("</head>", inlineCss);

			// Inject JS inside <script> tag
			String inlineJs = "<script>\n" + js + "\n</script>\n</body>";
			html = html.replace("</body>", inlineJs);

			// Save the updated HTML
			Files.writeString(Paths.get(reportPath), html, StandardCharsets.UTF_8);

			System.out.println("✔ Extent report successfully inlined for Jenkins.");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("❌ Failed to inline Extent report resources.");
		}
	}
}
