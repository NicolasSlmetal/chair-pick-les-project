package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.projections.ChairAvailableProjection;
import com.chairpick.ecommerce.projections.ChatBotResponse;
import com.chairpick.ecommerce.repositories.ChairRepository;
import com.chairpick.ecommerce.utils.filter.FilterObject;
import com.chairpick.ecommerce.utils.filter.TextQueryFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Service
public class ChatBotService {

    private final ChairRepository chairRepository;
    private final TextGenerationService generationService;
    private final EmbeddingService embeddingService;
    private final TextQueryFilter textQueryFilter;

    public ChatBotService(ChairRepository chairRepository, TextGenerationService generationService, EmbeddingService embeddingService, TextQueryFilter textQueryFilter) {
        this.chairRepository = chairRepository;
        this.generationService = generationService;
        this.embeddingService = embeddingService;
        this.textQueryFilter = textQueryFilter;
    }

    public Flux<ChatBotResponse> recommendChairByPrompt(String prompt) {
        float[] embedding = embeddingService.generateEmbedding(prompt);
        var result = textQueryFilter.filterByText(prompt);

        return Flux.fromIterable(getEitherWithFilterOrNot(embedding, result))
                .flatMap(chair -> {
                    String template = generateResponseTemplate(chair, prompt);


                    ChatBotResponse metadata = ChatBotResponse.builder()
                            .message(null)
                            .chair(chair)
                            .build();

                    Flux<ChatBotResponse> llmStream = generationService.generateAsyncResponse(template)
                            .map(textChunk -> ChatBotResponse.builder()
                                    .message(textChunk)
                                    .chair(chair)
                                    .build());

                    return Flux.concat(
                            Flux.just(metadata),
                            llmStream
                    );
                });


    }

    private List<ChairAvailableProjection> getEitherWithFilterOrNot(float[] embedding, FilterObject filter) {
        if (filter.hasFilters() && !filter.hasUndefinedFilters()) {

            return chairRepository.findBySemanticSearch(embedding, filter);
        }

        return chairRepository.findBySemanticSearch(embedding);
    }


    private static String generateResponseTemplate(ChairAvailableProjection chair, String prompt) {
        return String.format(
                """
                    Com base nas seguintes informações:
                    - Nome da cadeira: %s
                    - Descrição: %s
                    
                    E no interesse do usuário: %s
                    
                    Gere uma descrição curta (entre 10 e 20 palavras) destacando os benefícios e características da cadeira de forma envolvente e informativa.
                    
                    Restrições:
                    - O texto deve ser em português.
                    - Não inclua o nome da cadeira.
                    - Não use markdown, títulos, formatações ou raciocínio adicional.
                    - Forneça apenas a descrição final, sem explicações.
                    - Não repita a mesma descrição. Crie uma nova com base no prompt do usuário.
                    
                    Exemplo de estilo:
                    "Perfeita para o seu home office, oferece conforto, estilo e ergonomia em um só produto."
                    Siga sempre esse estilo.
                    """,
                chair.getName(),
                chair.getDescription(),
                prompt
        );
    }

}
