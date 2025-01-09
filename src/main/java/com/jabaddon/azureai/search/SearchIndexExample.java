package com.jabaddon.azureai.search;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.SearchDocument;
import com.azure.search.documents.indexes.SearchIndexClient;
import com.azure.search.documents.indexes.SearchIndexClientBuilder;
import com.azure.search.documents.util.SearchPagedIterable;

public class SearchIndexExample {
    public static void main(String[] args) {
        String endpoint = System.getenv("ENDPOINT");
        String key = System.getenv("API_KEY");

        if (endpoint == null || key == null) {
            System.out.println("Missing environment variable 'ENDPOINT' or 'API_KEY'.");
            System.out.println("Set them before running this sample.");
            System.exit(1);
        }

        SearchIndexClient searchIndexClient = new SearchIndexClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(key))
                .buildClient();

        SearchClient searchClient = searchIndexClient.getSearchClient("hotels-sample-index");
        SearchPagedIterable search = searchClient.search("new");
        search.forEach(result -> {
            var doc = result.getDocument(SearchDocument.class);
            System.out.printf("Hotel: %s,\nDescription: %s\n\n", doc.get("HotelName"), doc.get("Description"));
        });
    }
}
