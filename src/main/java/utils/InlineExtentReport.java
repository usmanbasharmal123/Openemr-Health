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

	private static String download(String url) throws Exception {
		HttpRequest request = HttpRequest.newBuilder(URI.create(url)).build();
		return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
	}

	public static void inlineResources(String reportPath) {
		try {
			String html = Files.readString(Paths.get(reportPath), StandardCharsets.UTF_8);

			// Extract CDN URLs from your HTML
			String cssUrl = "https://cdn.jsdelivr.net/gh/extent-framework/extent-github-cdn@6fbbd1c32fbc2463d026da56cce2e9eef0d29512/spark/css/spark-style.css";
			String jsUrl1 = "https://cdn.rawgit.com/extent-framework/extent-github-cdn/7cc78ce/spark/js/jsontree.js";
			String jsUrl2 = "https://cdn.jsdelivr.net/gh/extent-framework/extent-github-cdn@ac7abbc71b4bb073da00153a5a3fdbcfe0c95e2a/spark/js/spark-script.js";

			// Download CSS/JS
			String css = download(cssUrl);
			String js1 = download(jsUrl1);
			String js2 = download(jsUrl2);

			// Remove ALL external references
			html = html.replaceAll("<link[^>]*>", "");
			html = html.replaceAll("<script src=[^>]*></script>", "");

			// Inject CSS
			html = html.replace("</head>", "<style>" + css + "</style></head>");

			// Inject JS (both files)
			html = html.replace("</body>", "<script>" + js1 + js2 + "</script></body>");

			// Save updated HTML
			Files.writeString(Paths.get(reportPath), html, StandardCharsets.UTF_8);

			System.out.println("✔ Report successfully inlined for Jenkins.");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("❌ Failed to inline report.");
		}
	}
}
