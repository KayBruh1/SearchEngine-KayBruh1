package edu.usfca.cs272;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import edu.usfca.cs272.InvertedIndex.SearchResult;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
		@Override
		public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
			String query = request.getParameter("query");
			System.out.println("q " + query);
			String searchType = request.getParameter("searchType");
			System.out.println("s " + searchType);
			if (query != null) {
				boolean search = true;
				search = "exact".equals(searchType);
				Set<String> queries = Set.of(query.split("\\s+"));
				results = indexer.search(queries, search);
			}

			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);
			try (PrintWriter out = response.getWriter()) {
				String html = """
						    <!DOCTYPE html>
						    <html lang="en">
						    <head>
						        <meta charset="UTF-8">
						        <title>Search Engine</title>
						        <script>
						            function setPartial() {
						                document.getElementById('searchType').value = 'partial';
						            }
						            function setExact() {
						                document.getElementById('searchType').value = 'exact';
						            }
						        </script>
						    </head>
						    <body>
						        <h1>Search Engine</h1>
						        <form method="get" action="/search">
						            <p>
						                <input type="text" name="query" size="50" value="">
						            </p>
						            <p>
						                <button type="button" onclick="setPartial()">Partial Search</button>
						                <button type="button" onclick="setExact()">Exact Search</button>
						                <br>
						                <br>
						                <button type="submit">Enter</button>
						                <input type="hidden" name="searchType" id="searchType" value="partial">
						            </p>
						        </form>
						""";

				out.println(html);
				if (results != null && !results.isEmpty()) {
					out.println("<h2>Search Results:</h2>");
					out.println("<ol>");
					for (InvertedIndex.SearchResult result : results) {
						out.println(
								"<li><a href=\"" + result.getLocation() + "\">" + result.getLocation() + "</a></li>");
					}
					out.println("</ol>");
				} else if (query != null) {
					out.println("<h2>No results found.</h2>");
				}
			}
		}
	}
}