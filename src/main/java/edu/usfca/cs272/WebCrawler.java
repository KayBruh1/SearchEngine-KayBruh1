package edu.usfca.cs272;

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
	
	public void startCrawl(WebCrawler crawler, String seed, int total) throws URISyntaxException {
        for (int i = 0; i < total; i++) {
        	System.out.println(seed);
            crawler.crawl(new URI(seed));
        }	
	}

	public void crawl(URI uri) {
		if (!visited.contains(uri)) {
			visited.add(uri);
			workQueue.execute(new CrawlTask(uri, 3));
			workQueue.finish();
		}
	}

	private class CrawlTask implements Runnable {
		private final URI uri;
		private final int redirects;

		public CrawlTask(URI uri, int redirects) {
			this.uri = uri;
			this.redirects = redirects;
		}

		@Override
		public void run() {
			try {
				crawl(uri, redirects);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void crawl(URI uri, int redirects) {
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