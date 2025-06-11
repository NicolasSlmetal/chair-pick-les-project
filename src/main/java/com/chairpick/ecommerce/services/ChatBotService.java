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
import java.util.logging.Logger;

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
        String validationPrompt = generateValidationAnalysisPrompt(prompt);
        String validationResponse = generationService.generateResponse(validationPrompt).toLowerCase();

        System.out.println(validationResponse);
        if (validationResponse.contains("não")) {

            Logger.getGlobal().finer("Prompt não atende aos critérios de busca: " + prompt);
            return Flux.just(ChatBotResponse.builder()
                    .message("O prompt fornecido não atende aos critérios necessários para busca de cadeiras. Por favor, reformule o prompt.")
                    .build());
        }
        float[] embedding = embeddingService.generateEmbedding(prompt);
        FilterObject filter = textQueryFilter.filterByText(prompt);

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

    private List<ChairAvailableProjection> getEitherWithFilterOrNot(float[] embedding, FilterObject filter) {

        if (filter.hasFilters() && !filter.hasUndefinedFilters()) {

            return chairRepository.findBySemanticSearch(embedding, filter);
        }

        return chairRepository.findBySemanticSearch(embedding);
    }

    private static String generateValidationAnalysisPrompt(String prompt) {
        String template = """
            Você é um especialista em análise de texto para e-commerce de cadeiras. Sua tarefa é analisar o seguinte prompt considerando que ele será usado para buscar cadeiras em um e-commerce especializado em móveis residenciais e corporativos. O e-commerce NÃO vende cadeiras de veículos, cadeiras de rodas, cadeiras esportivas, nem itens para uso extremo, como trilhas ou montanhas.
            Prompt do usuário:
           "%s"
           Analise o prompt e verifique se ele atende aos seguintes critérios:
           1. Clareza: O prompt é claro e fácil de entender?
           2. Relevância: O prompt é relevante para o contexto de um e-commerce que vende apenas cadeiras para escritórios, salas de jantar, cozinha, varanda e ambientes internos?
           3. Linguagem: O prompt está escrito em português correto e adequado para o público-alvo?

           A resposta deve ser apenas "Sim" ou "Não", indicando se o prompt atende a todos os critérios acima. Se algum critério não for atendido — especialmente o de relevância — responda "Não".
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
