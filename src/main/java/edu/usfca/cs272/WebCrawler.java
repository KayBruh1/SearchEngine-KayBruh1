package edu.usfca.cs272;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Multithreaded class for web crawling
 */
public class WebCrawler {
	/**
	 * Set of visited uris
	 */
	private final HashSet<URI> visited;

	/**
	 * Thread safe inverted index instance for crawling
	 */
	private final ThreadSafeInvertedIndex indexer;

	/**
	 * Work queue instance for multithreading
	 */
	private final CustomWorkQueue workQueue;

	/**
	 * Constructs an indexer and work queue for building
	 * 
	 * @param indexer   The indexer to help with crawling
	 * @param workQueue The work queue for multithreading
	 */
	public WebCrawler(ThreadSafeInvertedIndex indexer, CustomWorkQueue workQueue) {
		this.indexer = indexer;
		this.workQueue = workQueue;
		visited = new HashSet<>();
	}

	/**
	 * Initiates the crawling process for the seed
	 * 
	 * @param seed  The URI to start crawling from
	 * @param total The total number of URIs to crawl
	 * @throws URISyntaxException If the syntax is invalid
	 */
	public void startCrawl(String seed, int total) throws URISyntaxException {
		crawl(new URI(seed), total);
		workQueue.finish();
	}

	/**
	 * Crawls the given URI and its hyperlinks
	 * 
	 * @param uri   The URI to crawl
	 * @param total The total number of URIs to crawl
	 */
	private void crawl(URI uri, int total) {
		if (visited.size() >= total || visited.contains(uri)) {
			return;
		}
		if (visited.size() < total) {
			visited.add(uri);
			workQueue.execute(new CrawlTask(uri, total));
		}
	}

	/**
	 * Class to help web crawl
	 */
	private class CrawlTask implements Runnable {
		/**
		 * The URI to crawl
		 */
		private final URI uri;

		/**
		 * The total number of URIs to crawl
		 */
		private final int total;

		/**
		 * @param uri   The URI to crawl
		 * @param total The number of URIs to crawl
		 */
		public CrawlTask(URI uri, int total) {
			this.uri = uri;
			this.total = total;
		}

		@Override
		public void run() {
			String htmlContent = HtmlFetcher.fetch(uri, 3);
			if (htmlContent != null) {
				String cleanedHtml = HtmlCleaner.stripBlockElements(htmlContent);
				ArrayList<URI> links = LinkFinder.listUris(uri, cleanedHtml);
				
				for (URI link : links) {
					if (visited.size() > total) {
						break;
					}
					if (!visited.contains(link)) {
						crawl(link, total);
					}
				}
				
				cleanedHtml = HtmlCleaner.stripTags(cleanedHtml);
				cleanedHtml = HtmlCleaner.stripEntities(cleanedHtml);
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