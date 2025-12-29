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
			if (body.contains("Moved Permanently") || body.contains("<html") || body.contains("<!DOCTYPE")) {
				System.out.println("❌ Invalid (HTML/redirect) response for: " + url);
				return "";
			}

			return body;

		} catch (Exception e) {
			System.out.println("❌ Failed to download: " + url);
			return "";
		}
	}

	public static void inlineResources(String reportPath) {
		try {
			String html = Files.readString(Paths.get(reportPath), StandardCharsets.UTF_8);

			// Spark CSS (dark theme compatible)
			String cssSparkStyle = safeDownload(
					"https://cdn.jsdelivr.net/gh/extent-framework/extent-github-cdn@7cc78ce/spark/css/spark-style.css");
			String cssSparkFonts = safeDownload(
					"https://cdn.jsdelivr.net/gh/extent-framework/extent-github-cdn@7cc78ce/spark/css/spark-fonts.css");
			String cssSparkIcons = safeDownload(
					"https://cdn.jsdelivr.net/gh/extent-framework/extent-github-cdn@7cc78ce/spark/css/spark-icons.css");

			// Spark JS
			String jsJsonTree = safeDownload(
					"https://cdn.jsdelivr.net/gh/extent-framework/extent-github-cdn@7cc78ce/spark/js/jsontree.js");
			String jsSparkScript = safeDownload(
					"https://cdn.jsdelivr.net/gh/extent-framework/extent-github-cdn@7cc78ce/spark/js/spark-script.js");

			// Validate downloads
			if (cssSparkStyle.isEmpty() || jsJsonTree.isEmpty() || jsSparkScript.isEmpty()) {
				System.out.println("❌ Missing Spark CSS/JS — skipping inline to avoid corrupting report.");
				return;
			}

			// Remove ALL <link> tags
			html = html.replaceAll("(?i)<link[^>]*>", "");

			// Remove ALL <script src="..."> tags
			html = html.replaceAll("(?i)<script[^>]*src=[\"'][^\"']*[\"'][^>]*></script>", "");
			html = html.replaceAll("(?i)<script[^>]*src=[\"'][^\"']*[\"'][^>]*>", "");

			// Inject ALL Spark CSS
			String allCss = "<style>" + cssSparkStyle + cssSparkFonts + cssSparkIcons + "</style>";

			html = html.replace("</head>", allCss + "</head>");

			// Inject ALL Spark JS
			String allJs = "<script>" + jsJsonTree + jsSparkScript + "</script>";

			int idx = html.lastIndexOf("</body>");
			if (idx != -1) {
				html = html.substring(0, idx) + allJs + html.substring(idx);
			}

			Files.writeString(Paths.get(reportPath), html, StandardCharsets.UTF_8);

			System.out.println("✔ FULL Spark inline completed (Dark Theme): " + reportPath);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("❌ Failed to inline report.");
		}
	}
}
