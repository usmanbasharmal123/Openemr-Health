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

			// Correct CDN URLs
			String cssUrl1 = "https://cdn.jsdelivr.net/gh/extent-framework/extent-github-cdn@6fbbd1c32fbc2463d026da5c6ce2e9eef0d29512/spark/css/spark-style.css";
			String cssUrl2 = "https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css";

			// FIXED jsontree.js URL
			String jsUrl1 = "https://cdn.jsdelivr.net/gh/extent-framework/extent-github-cdn@7cc78ce/spark/js/jsontree.js";

			// Download CSS/JS
			String css1 = download(cssUrl1);
			String css2 = download(cssUrl2);
			String js1 = download(jsUrl1);

			// Remove ALL <link> tags
			html = html.replaceAll("(?i)<link[^>]*>", "");

			// Remove ALL <script src="..."> tags
			html = html.replaceAll("(?i)<script[^>]*src=[\"'][^\"']*[\"'][^>]*></script>", "");
			html = html.replaceAll("(?i)<script[^>]*src=[\"'][^\"']*[\"'][^>]*>", "");

			// Inject CSS
			html = html.replace("</head>", "<style>" + css1 + css2 + "</style></head>");

			// Inject JS before last </body>
			int idx = html.lastIndexOf("</body>");
			if (idx != -1) {
				html = html.substring(0, idx) + "<script>" + js1 + "</script>" + html.substring(idx);
			}

			Files.writeString(Paths.get(reportPath), html, StandardCharsets.UTF_8);

			System.out.println("✔ Report successfully inlined for Jenkins: " + reportPath);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("❌ Failed to inline report.");
		}
	}
}
