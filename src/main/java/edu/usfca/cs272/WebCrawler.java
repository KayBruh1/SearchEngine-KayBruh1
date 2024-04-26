package edu.usfca.cs272;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;

public class WebCrawler {
	private final HashSet<URI> visited;
	
	/**
	 * Thread safe inverted index instance for crawling
	 */
	private final ThreadSafeInvertedIndex indexer;

	/**
	 * Work queue instance for multithreading
	 */
	private final CustomWorkQueue workQueue;

	public WebCrawler(ThreadSafeInvertedIndex indexer, CustomWorkQueue workQueue) {
		this.indexer = indexer;
		this.workQueue = workQueue;
		visited = new HashSet<>();
	}

	public void crawl(URI uri, int redirects) throws IOException, URISyntaxException {
		if (!visited.contains(uri)) {
			visited.add(uri);
			String htmlContent = HtmlFetcher.fetch(uri, redirects);
			if (htmlContent != null) {
				String cleanedHtml = HtmlCleaner.stripHtml(htmlContent);
				ArrayList<String> words = FileStemmer.listStems(cleanedHtml);
				int position = 0;
				for (String word : words) {
					position++;
					URI cleanURI = LinkFinder.clean(uri);
					indexer.addWord(word, cleanURI.toString(), position);
				}
			}
		}

	}
}