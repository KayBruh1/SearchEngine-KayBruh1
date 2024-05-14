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

/**
 * Class to process and display search results
 */
public class SearchEngine {
	/**
	 * Map to store search results
	 */
	private static List<SearchResult> results = null;

	/**
	 * Inverted index instance for searching
	 */
	private static ThreadSafeInvertedIndex indexer;

	/**
	 * @param indexer The indexer to help with crawling
	 */
	public SearchEngine(ThreadSafeInvertedIndex indexer) {
		SearchEngine.indexer = indexer;
	}

	/**
	 * @param port The port to use
	 * @throws Exception If an error occurs
	 */
	public void startEngine(int port) throws Exception {
		Server server = new Server(port);

		ServletHandler handler = new ServletHandler();
		handler.addServletWithMapping(SearchServlet.class, "/");

		server.setHandler(handler);
		server.start();

		System.out.println("Server started on port " + port);
		server.join();
	}

	/**
	 * Help process and display search results
	 */
	public static class SearchServlet extends HttpServlet {
		/**
		 * Default id
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
			String query = request.getParameter("query");
			String searchType = request.getParameter("searchType");
			if (query != null) {
				boolean search = "exact".equals(searchType);
//				Set<String> queries = Set.of(query.split("\\s+"));
				Set<String> queries = FileStemmer.uniqueStems(query);
				results = indexer.search(queries, !search);
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
						        <!-- Bootstrap CSS -->
						        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
						        <style>
						            .results {
						                margin-top: 30px;
						            }
						        </style>
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
						        <div class="container">
						            <h1 class="text-center mt-5">Search Engine</h1>
						            <form class="mt-5" method="get" action="/search">
						                <div class="input-group mb-3">
						                    <input type="text" class="form-control" name="query" placeholder="Search query">
						                    <div class="input-group-append">
						                        <button class="btn btn-outline-secondary" type="button" onclick="setPartial()">Partial Search</button>
						                        <button class="btn btn-outline-secondary" type="button" onclick="setExact()">Exact Search</button>
						                    </div>
						                </div>
						                <button type="submit" class="btn btn-primary">Enter</button>
						                <input type="hidden" name="searchType" id="searchType" value="partial">
						            </form>
						            <div class="results">
						            </div>
						        </div>
						        <!-- Bootstrap JS -->
						        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe" crossorigin="anonymous"></script>
						    </body>
						    </html>
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
				} else if (query != null && !query.isEmpty()) {
					out.println("<h2>No results found for query: " + query + "</h2>");
				}
			}
		}
	}
}