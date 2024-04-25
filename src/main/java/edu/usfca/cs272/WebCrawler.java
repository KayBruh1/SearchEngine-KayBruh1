package edu.usfca.cs272;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class WebCrawler {
	private final HashSet<URI> visited;

	public WebCrawler() {
		visited = new HashSet<>();
	}

	public void crawl(URI uri, int redirects, ThreadSafeInvertedIndex indexer) throws IOException, URISyntaxException {
		if (!visited.contains(uri)) {
			visited.add(uri);
			Map<String, List<String>> headers = HttpsFetcher.fetch(uri);
			int statusCode = HtmlFetcher.getStatusCode(headers);
			if (statusCode == 200 && HtmlFetcher.isHtml(headers)) {
				String htmlContent = HtmlFetcher.fetch(uri, redirects);
				if (htmlContent != null) {
					String cleanedHtml = HtmlCleaner.stripHtml(htmlContent);
					ArrayList<String> words = FileStemmer.listStems(cleanedHtml);
					int position = 0;
					for (String word : words) {
						position++;
						URI cleanURI = LinkFinder.clean(uri);
						System.out.println(cleanURI.toString());
						indexer.addWord(word, cleanURI.toString(), position);
					}
				}
			} else if (statusCode >= 300 && statusCode <= 399 && redirects > 0) {
				String redirect = HtmlFetcher.getRedirect(headers);
				if (redirect != null) {
					crawl(new URI(redirect), redirects - 1, indexer);
				}
			}
		}
	}
}