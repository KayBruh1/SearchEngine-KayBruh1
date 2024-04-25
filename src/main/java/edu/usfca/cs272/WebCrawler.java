package edu.usfca.cs272;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;

public class WebCrawler {
	private final HashSet<URI> visited;

	public WebCrawler() {
		visited = new HashSet<>();
	}

	public void crawl(URI uri, int redirects, ThreadSafeInvertedIndex indexer) {
		if (!visited.contains(uri)) {
			visited.add(uri);
			String htmlContent = HtmlFetcher.fetch(uri.toString(), redirects);
			String cleanedHtml = HtmlCleaner.stripHtml(htmlContent);
			ArrayList<String> words = FileStemmer.listStems(cleanedHtml);
			int position = 0;
			for (String word : words) {
				position++;
				indexer.addWord(word, uri.toString(), position);
				HashSet<URI> links = LinkFinder.uniqueUris(uri, htmlContent);
				for (URI link : links) {
					crawl(link, redirects - 1, indexer);
				}
			}
		}
	}
}
