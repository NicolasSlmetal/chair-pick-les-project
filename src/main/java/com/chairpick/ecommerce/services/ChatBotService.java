package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.projections.ChairAvailableProjection;
import com.chairpick.ecommerce.projections.ChatBotResponse;
import com.chairpick.ecommerce.repositories.ChairRepository;
import com.chairpick.ecommerce.utils.filter.FilterObject;
import com.chairpick.ecommerce.utils.filter.TextQueryFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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

    public void warmModel() {
        CompletableFuture.runAsync(() -> {
            generationService.generateResponse("Wake up");
        });
    }

    public Flux<ChatBotResponse> recommendChairByPrompt(String prompt) {
        String extractJson = generateTemplateJson(prompt);
        String extractedResponse = generationService.generateResponse(extractJson).toLowerCase();
        extractedResponse = sanitizeResponse(extractedResponse);
        FilterObject filter = textQueryFilter.filterByText(extractedResponse);
        if (filter == null || !filter.getRelevant()) {
            return Flux.just(ChatBotResponse.builder()
                    .message("Desculpe, não consegui entender o que você precisa. Por favor, tente reformular sua solicitação.")
                    .build());
        }
        String enhancedPrompt = prompt + (filter.getAdditionalKeywords() != null ? " " + filter.getAdditionalKeywords() : "");
        float[] embedding = embeddingService.generateEmbedding(enhancedPrompt);

        return Flux.fromIterable(getEitherWithFilterOrNot(embedding, filter))
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

    private static String sanitizeResponse(String extractedResponse) {
        return extractedResponse.replace("```json", "")
                .replace("```", "")
                .replaceAll("\\s+", " ");
    }

    private List<ChairAvailableProjection> getEitherWithFilterOrNot(float[] embedding, FilterObject filter) {
        return chairRepository.findBySemanticSearch(embedding, filter);
    }

    private static String  generateTemplateJson(String prompt) {
        String template = """
            Analise o texto abaixo e extraia os dados em formato JSON, seguindo esta estrutura:
            
            {
               "relevant": boolean,
               "limit": integer,
               "priceRange": null or [float | null, float | null],
               "widthRange": null or [float | null, float | null],
               "heightRange": null or [float | null, float | null],
               "lengthRange": null or [float | null, float | null],
               "weightRange": null or [float | null, float | null],
               "ratingRange": null or [float | null, float | null],
            }
            
            ### Regras para preenchimento:
            
            - **"relevant"**: deve ser `true` somente se o texto falar de cadeiras para uso interno, fixo ou portátil, em ambientes como sala de jantar, sala de estar, home office ou escritório comercial.
                  Deve ser `false` se o texto:
            
                  - mencionar uso automotivo ou externo (ex: colocar na Kombi, caminhão, carro ou jardim),
                  - for incoerente, exagerado ou fora do tema,
                  - envolver situações de improviso ou contextos muito específicos que fujam de um e-commerce comum.
                  
            - **"limit"**: número máximo de cadeiras solicitadas. Se o texto não informar claramente, use 3.
            
            - **"priceRange", "widthRange", "heightRange", "lengthRange", "weightRange", "ratingRange"**: listas com dois valores [mínimo, máximo].  
              - Se apenas um valor for citado, use `null` no outro.
              - Se nenhum valor for citado, use `null`.
              - Se o texto não fornecer informações suficientes, use `null` para todos os intervalos.
            
            - **"additionalKeywords"**: inclua apenas termos que descrevam estilo, tipo ou função da cadeira (ex: “ergonômica”, “executiva”, “dobrável”, etc).  
              - **Não inclua cor. Cores não devem ser consideradas ou extraídas.**
              - Se não houver palavras relevantes, use `null`.
            
            ### Exemplo de saída para o prompt "Quero atender clientes no meu escritório com cadeiras confortáveis e elegantes, de preferência ergonômicas e executivas, com preço entre R$250 e R$800, altura entre 90 e 110cm":
            
            
            {
                "relevant": true,
                "limit": 4,
                "priceRange": [250.0, 800.0],
                "widthRange": null,
                "heightRange": [90.0, 110.0],
                "lengthRange": null,
                "weightRange": null,
                "ratingRange": null,
                "additionalKeywords": "ergonômica,executiva"
            }
            Retorne apenas o JSON, sem formatação, explicações ou comentários adicionais.
            Texto a ser analisado: %s
            """;
        return String.format(template, prompt);
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
