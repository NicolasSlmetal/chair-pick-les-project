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

function saveMessageToSessionStorage(message) {
    const stored = sessionStorage.getItem("chatHistory");
    const history = stored ? JSON.parse(stored) : [];
    console.log({ message });
    history.push(message);
    history.filter(item => item.type === "product-group").forEach(item => item.products.forEach(product => {
        product.description = product.description.replace("null", "");
    }));
    sessionStorage.setItem("chatHistory", JSON.stringify(history));
}

function restoreChatFromSessionStorage() {
    const stored = sessionStorage.getItem("chatHistory");
    if (!stored) return;
    const history = JSON.parse(stored);
    let lastContainer = null;
    for (const item of history) {
        if (item.type === "user") {
            lastContainer = createMessageElement(item.text, "USER");
            chatContainer.appendChild(lastContainer);
            activeElement(lastContainer, 10);
        } else if (item.type === "product-group") {
            const messageGroup = document.createElement("p");
            for (const product of item.products) {
                const productCard = buildProductCard(product, product.description);
                messageGroup.appendChild(productCard);
            }
            lastContainer = createMessageElement(messageGroup, "BOT");
            chatContainer.appendChild(lastContainer);
            activeElement(lastContainer, 10);
        } else {
            lastContainer = createMessageElement(item.text, "BOT");
            chatContainer.appendChild(lastContainer);
            activeElement(lastContainer, 10);
        }
    }
    if (lastContainer) {
        lastContainer.scrollIntoView({ behavior: "smooth" });
    }

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

    saveMessageToSessionStorage({ type: "user", text: message });

    generateTextAnswerBasedInUserInput(message);
}

function generateTextAnswerBasedInUserInput(prompt) {
    const eventSource = new EventSource(`/chatbot/chairs?prompt=${encodeURIComponent(prompt)}`);
    const displayedChairs = new Map();
    let createdBotAnswerContainer = undefined;

    const productsToSave = [];

    eventSource.onmessage = (event) => {
        const data = JSON.parse(event.data);
        const chair = data.chair;

        if (!displayedChairs.has(chair.id)) {
            const generatedCard = buildProductCard(chair, data.message);
            const productData = { id: chair.id, name: chair.name, description: data.message };
            if (productData.description !== null && productData.description !== undefined) {
                productData.description = data.message.replace("null", "");
            }
            productsToSave.push(productData);

            if (!createdBotAnswerContainer) {
                const botAnswerContainer = createMessageElement(generatedCard, "BOT");
                chatContainer.appendChild(botAnswerContainer);
                botAnswerContainer.scrollIntoView({ behavior: "smooth" });
                activeElement(botAnswerContainer, 100);
                createdBotAnswerContainer = botAnswerContainer;
            } else {
                const messageTextElement = messageContainerMap.get(createdBotAnswerContainer);
                messageTextElement.appendChild(generatedCard);
            }

            displayedChairs.set(chair.id, productData);
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

            descriptionElement.innerText += data.message;
            displayedChairs.get(chair.id).description += data.message;
        }
    };

    eventSource.onerror = (error) => {
        console.error("SSE connection error:", error);
        if (displayedChairs.size === 0) {
            const errorMessage = createMessageElement("Desculpe, não consegui encontrar cadeiras para a sua solicitação.", "BOT");
            saveMessageToSessionStorage({type: "not-found", text: "Desculpe, não consegui encontrar cadeiras para a sua solicitação."})
            chatContainer.appendChild(errorMessage);
            activeElement(errorMessage, 100);
            errorMessage.scrollIntoView({ behavior: "smooth" });
        } else {
            saveMessageToSessionStorage({ type: "product-group", products: Array.from(displayedChairs.values()) });
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
    description.innerText = descriptionText || "";
    card.appendChild(description);

    card.href = "/chairs/" + product.id;
    container.appendChild(card);
    descriptionMap.set(product.id, description);

    if (!descriptionText) {
        description.innerText = "Carregando descrição...";
        const interval = setInterval(() => {
            const dotsCountMap = new Map([
                ["0", "."],
                ["1", ".."],
                ["2", "..."],
                ["3", "."]
            ]);
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
    restoreChatFromSessionStorage();
}

window.onload = init;
window.onkeydown = (event) => {
    if (event.key === "Enter") {
        button.click();
    }
};