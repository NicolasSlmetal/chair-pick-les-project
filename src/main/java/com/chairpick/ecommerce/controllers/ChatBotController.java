package com.chairpick.ecommerce.controllers;

import com.chairpick.ecommerce.projections.ChatBotResponse;
import com.chairpick.ecommerce.services.ChatBotService;
import org.springframework.http.MediaType;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chatbot")
public class ChatBotController {

    private final ChatBotService chatBotService;

    public ChatBotController(ChatBotService chatBotService) {
        this.chatBotService = chatBotService;
    }

    @GetMapping
    public ModelAndView redirectToChatbot() {
        ModelAndView view = new ModelAndView();
        view.addObject("pageTitle", "Chatbot");
        view.setViewName("chatbot/index.html");
        return view;
    }

    @GetMapping(path =  "/chairs", produces = MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
    public Flux<ChatBotResponse> findChairsByPrompt(@RequestParam("prompt") String prompt) {
        return chatBotService.recommendChairByPrompt(prompt);
    }

}
