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
			System.out.println(query);
			System.out.println("index " + indexer.toString());
			if (query != null) {
				Set<String> queries = Set.of(query.split("\\s+"));
				results = indexer.search(queries, true);
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
						</head>
						<body>
						    <h1>Search Engine</h1>
						    <form method="get" action="/search">
						        <p>
						            <input type="text" name="query" size="50" value=""></input>
						        </p>
						        <p>
						            <button type="submit">Search</button>
						        </p>
						    </form>
						    <h2>Search Results:</h2>
						""";

				out.println(html);
				if (results != null && !results.isEmpty()) {
					out.println("<ol>");
					for (InvertedIndex.SearchResult result : results) {
						out.println(
								"<li><a href=\"" + result.getLocation() + "\">" + result.getLocation() + "</a></li>");
					}
					out.println("</ol>");
				} else if (query != null) {
					out.println("<p>No results found.</p>");
				}
			}
		}
	}
}