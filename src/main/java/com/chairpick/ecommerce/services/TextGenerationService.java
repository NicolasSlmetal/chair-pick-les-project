package com.chairpick.ecommerce.services;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TextGenerationService {

    private final ChatModel chatClient;
    private final Map<String, String> promptCache = new HashMap<>();

    public TextGenerationService(ChatModel chatClient) {
        this.chatClient = chatClient;
    }

    public String generateResponse(String prompt) {

        Prompt promptOptions = new Prompt(prompt, ChatOptions.builder()
                .maxTokens(5)
                .temperature(0.0)
                .stopSequences(List.of("\n"))
                .build());
        return chatClient.call(promptOptions).getResult().getOutput().getText();
    }

    public Flux<String> generateAsyncResponse(String prompt) {

        String sanitizedPrompt = prompt.toUpperCase();
        if (promptCache.containsKey(sanitizedPrompt)) {
            return Flux.just(promptCache.get(sanitizedPrompt));
        }
        StringBuilder responseBuilder = new StringBuilder();
        Prompt promptInstruction = new Prompt(prompt, ChatOptions.builder()
                .build());
        return chatClient.stream(promptInstruction)
            .map(
                    chatResponse -> {
                        String response = chatResponse.getResult().getOutput().getText();
                        response = response.replaceAll("\"", "");
                        responseBuilder.append(response);
                        return response;
                    })
                .doOnComplete(() -> {
                    String fullResponse = responseBuilder.toString();
                    promptCache.put(sanitizedPrompt, fullResponse);
                });

    }
}
