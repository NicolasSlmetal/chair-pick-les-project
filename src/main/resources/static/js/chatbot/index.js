// Refatorado para uso com Flux e EventSource no mesmo endpoint
import { inputText, chatContainer, button } from "./constants.js";
const descriptionMap = new Map();
const messageContainerMap = new Map();
const loadingIntervalMap = new Map();

const classMapping = new Map([
    ["USER", "user__message"],
    ["BOT", "bot__message"]
]);

const imageMapping = new Map([
    ["USER", "assets/user.png"],
    ["BOT", "assets/bot.png"]
]);

function generateText(text) {
    const messageText = document.createElement("p");
    messageText.classList.add("message__text");

    if (text instanceof HTMLElement) {
        messageText.appendChild(text);
    } else {
        messageText.textContent = text;
    }

    return messageText;

}

function createMessageElement(text, origin = "USER") {
    const messageContainer = document.createElement("div");
    messageContainer.classList.add("message__container", classMapping.get(origin));

    const messageText = generateText(text);

    messageContainer.appendChild(messageText);

    const image = document.createElement("img");
    image.classList.add("profile__image");
    image.src = imageMapping.get(origin);
    messageContainer.appendChild(image);
    messageContainerMap.set(messageContainer, messageText);
    return messageContainer;
}

function activeElement(element, time) {
    setTimeout(() => {
        element.classList.add("active");
    }, time);
}

function sendMessage() {
    const message = inputText.value.trim();
    if (!message) return;

    inputText.value = "";

    const userPromptContainer = createMessageElement(message);
    chatContainer.appendChild(userPromptContainer);
    userPromptContainer.scrollIntoView({ behavior: "smooth" });
    activeElement(userPromptContainer, 10);
    descriptionMap.clear();
    generateTextAnswerBasedInUserInput(message);
}

function generateTextAnswerBasedInUserInput(prompt) {
    const eventSource = new EventSource(`/chatbot/chairs?prompt=${encodeURIComponent(prompt)}`);

    const displayedChairs = new Set();
    let createdBotAnswerContainer = undefined;
    eventSource.onmessage = (event) => {
        const data = JSON.parse(event.data);
        const chair = data.chair;

        if (!displayedChairs.has(chair.id)) {
            const generatedCard =  buildProductCard(chair, data.message)
            if (!createdBotAnswerContainer) {
                const botAnswerContainer = createMessageElement(
                    generatedCard,
                    "BOT"
                );
                chatContainer.appendChild(botAnswerContainer);
                botAnswerContainer.scrollIntoView({ behavior: "smooth" });
                activeElement(botAnswerContainer, 100);
                createdBotAnswerContainer = botAnswerContainer;
            } else {
                const messageTextElement = messageContainerMap.get(createdBotAnswerContainer);
                messageTextElement.appendChild(generatedCard);
            }
            displayedChairs.add(chair.id);
        } else {
            const descriptionElement = descriptionMap.get(chair.id);
            const interval = loadingIntervalMap.get(chair.id);

            if (interval) {
                clearInterval(interval);
                loadingIntervalMap.delete(chair.id);
                descriptionElement.innerText = descriptionElement.innerText.replace("Carregando descrição", "");
                while (descriptionElement.innerText.endsWith(".")) {
                    descriptionElement.innerText = descriptionElement.innerText.slice(0, -1);
                }
            }

            descriptionMap.get(chair.id).innerText += data.message;
        }
    };

    eventSource.onerror = (error) => {
        console.error("SSE connection error:", error);
        if (displayedChairs.size === 0) {
            const errorMessage = createMessageElement("Desculpe, não consegui encontrar cadeiras para a sua solicitação.", "BOT");
            chatContainer.appendChild(errorMessage);
            errorMessage.scrollIntoView({ behavior: "smooth" });
            activeElement(errorMessage, 100);
        }
        eventSource.close();
    };
}

function buildProductCard(product, descriptionText) {
    const container = document.createElement("p");

    const card = document.createElement("a");
    card.className = "product__card";
    card.innerHTML = `
        <strong>${product.name}</strong><br>
        <img src="/images/chairs/${product.id}" alt="${product.name}" class="product__image"><br>
    `;

    const description = document.createElement("p");
    description.innerText = descriptionText;
    card.appendChild(description);

    card.href = "/chairs/" + product.id;
    container.appendChild(card);
    descriptionMap.set(product.id, description);
    console.log({ product, descriptionText });
    if (!descriptionText) {
        description.innerText = "Carregando descrição...";
        const interval = setInterval(() => {
            const dotsCountMap = new Map();
            dotsCountMap.set("0", ".");
            dotsCountMap.set("1", "..");
            dotsCountMap.set("2", "...");
            dotsCountMap.set("3", ".");
            const text = description.innerText;
            const textWithoutDots = text.replace(/\.+$/, "");
            const dotsNumber = text.length - textWithoutDots.length;
            const nextDots = dotsCountMap.get(dotsNumber.toString());
            description.innerText = `${textWithoutDots}${nextDots}`;
        }, 500);
        loadingIntervalMap.set(product.id, interval);

    }
    return container;
}

function init() {
    button.addEventListener("click", sendMessage);
}

window.onload = init;
window.onkeydown = (event) => {
    if (event.key === "Enter") {
        button.click();
    }
};
