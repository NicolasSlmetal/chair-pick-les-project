function compareObjects(value1, value2) {
    if (typeof value1 === "object" && typeof value2 === "object") {
        return JSON.stringify(value1) === JSON.stringify(value2);
    }
    if (typeof value1 === "string" && typeof value2 === "string") {
        return value1.trim().toUpperCase() === value2.trim().toUpperCase();
    }

    return value1 === value2;
}

function getObjectFromInputs(section) {
    const inputs = section.querySelectorAll("input, select, textarea");
    const object = {};
    inputs.forEach(input => {
        object[input.id] = input.value;
    });
    return object;
}


export function setupValidationForSectionInputs(section, array, errors, buttonId = "", crucialFields = []) {
    const button = section.querySelector("button" + buttonId ? `#${buttonId}` : "");
    const objectFromInputs = getObjectFromInputs(section);
    const p = section.querySelector("p.already_exists_alert");
    p.innerText = "";

    const inputs = section.querySelectorAll("input, select");
    for (const input of inputs) {
        if (errors.has(input.id)) {
            button.setAttribute("disabled", "disabled");
            button.classList.add("disabled");
            return;
        }
    }

    button.removeAttribute("disabled");
    button.classList.remove("disabled");
    array.forEach(element => {
        let isEqual = true;
        Object.keys(element)
        .filter(key => Object.keys(objectFromInputs).includes(key) && crucialFields.includes(key))
        .forEach(key => {
            isEqual &&= compareObjects(element[key], objectFromInputs[key]);
            if (!isEqual) {
                p.innerText = "";
                return;
            }
        });
        if (isEqual) {
            p.innerText = "O item já foi cadastrado";
            button.setAttribute("disabled", "disabled");
            button.classList.add("disabled");
            return;
        }
    });


}

function replaceInputsForACard(section, array, customTitle = "Card", id = "") {
    const item = {};
    const cardsDiv = section.querySelector(".cards");
    const inputs = section.querySelectorAll("input, select, textarea");
    const labelMap = new Map();
    section.querySelectorAll("label").forEach(label => {
        labelMap.set(label.getAttribute("for"), label.textContent);
    });
    const card = document.createElement("div");
    const cardTitle = document.createElement("h2");
    const cardNumber = section.querySelectorAll(".card").length + 1;
    cardTitle.textContent = `${customTitle} - ${cardNumber}` ;
    card.appendChild(cardTitle);
    card.classList.add("card");
    const inputsIds = new Map();
    inputs.forEach(input => {
        if (input.value) {
            const p = document.createElement("p");
            item[input.id] = input.value;
            inputsIds.set(labelMap.get(input.id) ,input.id);
            const value = input instanceof HTMLSelectElement ? input.options[input.selectedIndex].text : input.value;
            p.textContent = `${labelMap.get(input.id)} - ${value}`;

            card.appendChild(p);
        }
    });
    item["isDefaultOption"] = false;
    card.setAttribute("is_default_option", false);
    const removeButton = document.createElement("a");
    removeButton.classList.add("action__button");
    removeButton.classList.add("danger");
    removeButton.textContent = "Remover";
    removeButton.type = "button";
    const callBack = () => {
        setupRemoveCardButton(section);
        changeCardsNamesOnRemove(section, customTitle);
        configureDefaultOption(section, array);
    }
    removeButton.addEventListener("click", (event) => {
        event.stopPropagation();
        array.splice(array.indexOf(item), 1);
        removeCard(card, callBack);
     });
    card.appendChild(removeButton);
    inputs.forEach(input => {
        document.querySelector(`label[for="${input.id}"]`).style.display = "none";
        input.value = input instanceof HTMLSelectElement ? input.options[input.selectedIndex].text : "";
        input.style.display = "none";
    });

    card.addEventListener("click", () => {

        if (card.getAttribute("is_default_option") === "true") {
            return;
        }
        array.forEach(item => {
            item.isDefaultOption = false;
        });
        const selectedIndex = array.findIndex(element => compareObjects(element, item));
        array[selectedIndex].isDefaultOption = true;

        const prevDefaultOption = findDefaultOption(section);

        if (prevDefaultOption) {
            prevDefaultOption.setAttribute("is_default_option", "false");
        }
        card.setAttribute("is_default_option", "true");
        detachDefaultOption(section);

        setupRemoveCardButton(section);
    });
    section.querySelector(`button${id ? `#${id}`:""}`).style.display = "none";
    cardsDiv.appendChild(card);
    section.focus();
    console.log({array});
    return item;
}


export function setupButtonInSection(section, array, customTitle = "Card", id = "", callBack = null) {
    const button = section.querySelector("button" + (id ? `#${id}` : ""));
    button.addEventListener("click", () => {
        const data = replaceInputsForACard(section, array, customTitle, id);
        array.push(data);

        setupRemoveCardButton(section);
        configureDefaultOption(section, array);
        if (callBack) {
            callBack();
        }
    });
}

export function configureDefaultOption(section, array) {
    const cards = section.querySelectorAll(".card");
    if (cards.length === 1) {
        cards[0].setAttribute("is_default_option", "true");
        cards[0].classList.add("default");
        cards[0].querySelector("h2").textContent = `${cards[0].querySelector("h2").textContent.replace("(Padrão)", "")} (Padrão)`;
    }

    if (array.length === 1) {
        array[0].isDefaultOption = true;
    }
}

export function findDefaultOption(section) {
    const cards = section.querySelectorAll(".card");
    for (const card of cards) {
        if (card.getAttribute("is_default_option") === "true") {
            return card;
        }
    }
    return null;
}

export function detachDefaultOption(section) {
    const cards = section.querySelectorAll(".card");
    cards.forEach(card => {
        const title = card.querySelector("h2");
        if (card.getAttribute("is_default_option") === "true") {
            card.classList.add("default");
            title.textContent = `${title.textContent} (Padrão)`;
        } else {
            card.classList.remove("default");
            title.textContent = title.textContent.replace(" (Padrão)", "").trim();
        }
    });
}


export function verifyIfCanRemoveCard(buttons) {
    return buttons.length > 1;
}

export function setupRemoveCardButton(section) {

    const cardsDiv = section.querySelector(".cards");
    const removeButtons = cardsDiv.querySelectorAll(".action__button.danger");
    if (!verifyIfCanRemoveCard(removeButtons)) {
        removeButtons.forEach(button => {
            button.style.display = "none";
        });
        return;
    }

    removeButtons.forEach(button => {
        if (button.parentNode.getAttribute("is_default_option") === "true" || button.parentNode.classList.contains("default")) {
            button.style.display = "none";
            return;
        }
        button.style.display = "block";
    });


}

function changeCardsNamesOnRemove(section, customTitle = "Card") {
    const cards = section.querySelectorAll(".card");
    cards.forEach((card, index) => {
        card.querySelector("h2").textContent = `${customTitle} - ${index + 1} ${card.getAttribute("is_default_option") === "true" ? "(Padrão)" : ""}`;
    });
}

export function removeCard(card, callback = null) {
    card.remove();
    if (callback) {
        callback();
    }
}