package edu.usfca.cs272;

import java.util.List;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import edu.usfca.cs272.InvertedIndex.SearchResult;
import jakarta.servlet.http.HttpServlet;

public class SearchEngine {
	/**
	 * Map to store search results
	 */
	private static List<SearchResult> results = null;

	/**
	 * Inverted index instance for searching
	 */
	private static ThreadSafeInvertedIndex indexer;

	public SearchEngine(ThreadSafeInvertedIndex indexer) {
		this.indexer = indexer;
	}

	public void startEngine(int port) throws Exception {
		Server server = new Server(port);

		ServletHandler handler = new ServletHandler();
		handler.addServletWithMapping(SearchServlet.class, "/");

		server.setHandler(handler);
		server.start();

		System.out.println("Server started on port " + port);
		server.join();
	}

	public static class SearchServlet extends HttpServlet {
	}
}