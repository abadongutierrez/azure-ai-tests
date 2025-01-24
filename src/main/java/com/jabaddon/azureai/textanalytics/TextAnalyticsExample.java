package com.jabaddon.azureai.textanalytics;

import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.TextAnalyticsClientBuilder;
import com.azure.ai.textanalytics.models.DocumentSentiment;
import com.azure.core.credential.AzureKeyCredential;
import com.jabaddon.azureai.EnvVarCollector;

import java.util.List;

public class TextAnalyticsExample {
    public static void main(String[] args) {
        List<String> vars = EnvVarCollector.collectEnv("ENDPOINT", "KEY");

        TextAnalyticsClient textAnalyticsClient = new TextAnalyticsClientBuilder()
                .credential(new AzureKeyCredential(vars.get(1)))
                .endpoint(vars.get(0))
                .buildClient();

        String document = "The hotel was dark and unclean. I like microsoft.";
        DocumentSentiment documentSentiment = textAnalyticsClient.analyzeSentiment(document);
        System.out.printf("Analyzed document sentiment: %s\n", documentSentiment.getSentiment());
        documentSentiment.getSentences().forEach(sentenceSentiment ->
                System.out.printf("Analyzed sentence sentiment: %s - %s\n", sentenceSentiment.getText(), sentenceSentiment.getSentiment()));
    }
}
