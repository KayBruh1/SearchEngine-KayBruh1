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

	public void startCrawl(String seed, int total) throws URISyntaxException {
		for (int i = 0; i < total; i++) {
			crawl(new URI(seed), total);
		}
		workQueue.finish();
	}

	public void crawl(URI uri, int total) {
		if (visited.size() >= total || visited.contains(uri)) {
			return;
		}

		visited.add(uri);
		if (visited.size() <= total) {
			workQueue.execute(new CrawlTask(uri, total));
		}
	}

	private class CrawlTask implements Runnable {
		private final URI uri;

		private final int total;

		public CrawlTask(URI uri, int total) {
			this.uri = uri;
			this.total = total;
		}

		@Override
		public void run() {
			try {
				String htmlContent = HtmlFetcher.fetch(uri, 3);
				if (htmlContent != null) {
					ArrayList<URI> links = LinkFinder.listUris(uri, htmlContent);
					String cleanedHtml = HtmlCleaner.stripHtml(htmlContent);
					//System.out.println(htmlContent);
					//System.out.println(links);
					ArrayList<String> words = FileStemmer.listStems(cleanedHtml);
					
					int position = 0;
					for (String word : words) {
						position++;
						URI cleanURI = LinkFinder.clean(uri);
						indexer.addWord(word, cleanURI.toString(), position);
					}

					for (URI link : links) {
						if (!visited.contains(link)) {
							crawl(link, total);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}