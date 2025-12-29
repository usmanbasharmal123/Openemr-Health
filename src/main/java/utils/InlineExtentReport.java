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

			// Reject redirects, HTML error pages, or empty responses
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

			// Correct CDN URLs
			String cssUrl1 = "https://cdn.jsdelivr.net/gh/extent-framework/extent-github-cdn@6fbbd1c32fbc2463d026da5c6ce2e9eef0d29512/spark/css/spark-style.css";
			String cssUrl2 = "https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css";

			// Correct jsontree.js URL
			String jsUrl1 = "https://cdn.jsdelivr.net/gh/extent-framework/extent-github-cdn@7cc78ce/spark/js/jsontree.js";

			// SAFE downloads
			String css1 = safeDownload(cssUrl1);
			String css2 = safeDownload(cssUrl2);
			String js1 = safeDownload(jsUrl1);

			// If downloads failed, DO NOT corrupt the report
			if (css1.isEmpty() || js1.isEmpty()) {
				System.out.println("❌ CSS/JS download failed — skipping inline to avoid corrupting report.");
				return;
			}

			// Remove ALL <link> tags
			html = html.replaceAll("(?i)<link[^>]*>", "");

			// Remove ALL <script src="..."> tags
			html = html.replaceAll("(?i)<script[^>]*src=[\"'][^\"']*[\"'][^>]*></script>", "");
			html = html.replaceAll("(?i)<script[^>]*src=[\"'][^\"']*[\"'][^>]*>", "");

			// Inject CSS safely
			html = html.replace("</head>", "<style>" + css1 + css2 + "</style></head>");

			// Inject JS safely before last </body>
			int idx = html.lastIndexOf("</body>");
			if (idx != -1) {
				html = html.substring(0, idx) + "<script>" + js1 + "</script>" + html.substring(idx);
			}

			// Save updated HTML
			Files.writeString(Paths.get(reportPath), html, StandardCharsets.UTF_8);

			System.out.println("✔ SAFE inline completed for: " + reportPath);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("❌ Failed to inline report.");
		}
	}
}
