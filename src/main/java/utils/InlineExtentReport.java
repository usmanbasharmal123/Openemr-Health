package utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class InlineExtentReport {

	private static final HttpClient client = HttpClient.newHttpClient();

	private static String safeDownload(String url) {
		try {
			HttpRequest request = HttpRequest.newBuilder(URI.create(url)).build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			String body = response.body();
			if (body == null || body.isBlank()) {
				System.out.println("❌ Empty response for: " + url);
				return "";
			}

			// If CDN returns HTML or an error message, don't inject it
			if (body.contains("Couldn't find the requested file") || body.contains("<html")
					|| body.contains("<!DOCTYPE")) {
				System.out.println("❌ Invalid response for: " + url);
				return "";
			}

			return body;

		} catch (Exception e) {
			System.out.println("❌ Failed to download: " + url + " -> " + e.getMessage());
			return "";
		}
	}

	public static void inlineResources(String reportPath) {
		try {
			String html = Files.readString(Paths.get(reportPath), StandardCharsets.UTF_8);

			// ExtentReports 4.1.7 Spark v4 CDN paths
			String base = "https://cdn.jsdelivr.net/gh/extent-framework/extent-github-cdn@v4.1.7/spark/v4";

			// Spark CSS (dark theme compatible)
			String cssSparkStyle = safeDownload(base + "/css/spark-style.css");
			String cssSparkFonts = safeDownload(base + "/css/spark-fonts.css");
			String cssSparkIcons = safeDownload(base + "/css/spark-icons.css");

			// Spark JS
			String jsJsonTree = safeDownload(base + "/js/jsontree.js");
			String jsSparkScript = safeDownload(base + "/js/spark-script.js");

			// If any core pieces are missing, don't corrupt the report
			if (cssSparkStyle.isEmpty() || jsJsonTree.isEmpty() || jsSparkScript.isEmpty()) {
				System.out.println("❌ Missing core Spark CSS/JS — skipping inline to avoid corrupting report.");
				return;
			}

			// Remove ALL external <link> tags
			html = html.replaceAll("(?i)<link[^>]*>", "");

			// Remove ALL <script src="..."> tags
			html = html.replaceAll("(?i)<script[^>]*src=[\"'][^\"']*[\"'][^>]*></script>", "");
			html = html.replaceAll("(?i)<script[^>]*src=[\"'][^\"']*[\"'][^>]*>", "");

			// Inject ALL Spark CSS inline
			String allCss = "<style>" + cssSparkStyle + cssSparkFonts + cssSparkIcons + "</style>";

			html = html.replace("</head>", allCss + "</head>");

			// Inject ALL Spark JS inline (at bottom of body)
			String allJs = "<script>" + jsJsonTree + jsSparkScript + "</script>";

			int idx = html.lastIndexOf("</body>");
			if (idx != -1) {
				html = html.substring(0, idx) + allJs + html.substring(idx);
			}

			Files.writeString(Paths.get(reportPath), html, StandardCharsets.UTF_8);

			System.out.println("✔ FULL Spark inline completed for Extent 4.1.7 (Dark Theme): " + reportPath);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("❌ Failed to inline report.");
		}
	}
}
