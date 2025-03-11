import {
    deliveryAddressName,
    deliveryAddressInput,
    deliveryAddressObservationsInput,
    birthdateInput,
    deliveryCepInput,
    deliveryCityInput,
    cpfInput,
    emailInput,
    form,
    nameInput,
    deliveryNeighborhoodInput,
    deliveryNumberInput,
    phoneInput,
    deliveryStateInput,
    submitButton,
    deliveryCountryInput,
    deliveryAddressSection,
    deliveryAddressType,
    cardNumberInput,
    cardNameInput,
    cardVerificationCodeInput,
    cardBrandInput,
    cardSection,
    phoneTypeInput,
    genreInput,
    passwordInput,
    passwordConfirmationInput
} from "../../consts.js";
import { setupButtonInSection, setupValidationForSectionInputs } from "./configSection.js";
import { postCustomer } from "./postCustomer.js";

deliveryAddressSection.setAttribute("tabindex", "-1");
cardSection.setAttribute("tabindex", "-1");

const deliveryAddresses = [];
const creditCards = [];

const deliveryAddressCrucialInputs = [
    deliveryCepInput,
    deliveryNumberInput,
    deliveryNeighborhoodInput,
    deliveryCityInput,
    deliveryStateInput,
    deliveryCountryInput,
    deliveryAddressInput,
].map(input => input.id);

const creditCardCrucialFields = [
    cardNumberInput,
    cardNameInput,
    cardBrandInput,
    cardVerificationCodeInput,
].map(input => input.id);

function normalizeOnlyCrucialFields(address, type) {
    return {
        cep: address[`cep_${type}`],
        street: address[`address_${type}`],
        number: address[`number_${type}`],
        neighborhood: address[`neighborhood_${type}`],
        city: address[`city_${type}`],
        state: address[`state_${type}`],
        country: address[`country_${type}`],
    }
}

function normalize(address, type) {
    address[`${type}_observations`] = address[`${type}_observations`] ? address[`${type}_observations`] : "";
    return {
        name: address[`name_${type}`],
        cep: address[`cep_${type}`],
        street: address[`address_${type}`],
        streetType: address[`address_${type}_type`],
        number: address[`number_${type}`],
        neighborhood: address[`neighborhood_${type}`],
        city: address[`city_${type}`],
        state: address[`state_${type}`],
        country: address[`country_${type}`],
        observations: address[`${type}_observations`],
    }
}

function compareCrucialFields(address1, address2) {
    return address1.cep === address2.cep &&
        address1.street === address2.street &&
        address1.number === address2.number &&
        address1.neighborhood === address2.neighborhood &&
        address1.city === address2.city &&
        address1.state === address2.state &&
        address1.country === address2.country;
}


form.addEventListener("submit", async (event) => {
    event.preventDefault();
    const deliveryAddressCopy = [...deliveryAddresses];

    const data = Object.fromEntries(new FormData(form));
    Object.keys(data).forEach(key => {
        if (data[key] === "") {
            delete data[key];
        }
    })
    data["addresses"] = [];

    deliveryAddressCopy.forEach(address => {
        const preparedAddress = {};
        preparedAddress["name"] = address["name_delivery"],
        preparedAddress["isDefault"] = address["isDefaultOption"],
        preparedAddress["observations"] = address["delivery_observations"] ? address["delivery_observations"] : "";
        preparedAddress["cep"] = address["cep_delivery"];
        preparedAddress["street"] = address["address_delivery"];
        preparedAddress["streetType"] = address["address_delivery_type"].toUpperCase();
        preparedAddress["number"] = address["number_delivery"];
        preparedAddress["neighborhood"] = address["neighborhood_delivery"];
        preparedAddress["city"] = address["city_delivery"];
        preparedAddress["state"] = address["state_delivery"];
        preparedAddress["country"] = address["country_delivery"];
        data["addresses"].push(preparedAddress);
    });

    data["creditCards"] = [...creditCards];
    data["birthDate"] = data["birthdate"];
    data["phoneType"] = data["phone_type"].toUpperCase();
    data["creditCards"].forEach(card => {
        card["brand"] = card["card_brand"].toUpperCase();
        card["number"] = card["card_number"];
        card["name"] = card["card_name"];
        card["isDefault"] = card["isDefaultOption"];
    });
    console.log({data});
    await postCustomer(data);
})


const nonRequiredInputs = [
    deliveryAddressObservationsInput,
];

const globalErrors = new Map();

const controlInsertMap = new Map();
const controlTitleMap = new Map();
controlTitleMap.set("delivery", "endereços de entrega");

controlTitleMap.set("credit_card", "cartões de crédito");

controlInsertMap.set("delivery", false);
controlInsertMap.set("credit_card", false);

function validateEmptyness(input) {
    return input.value ? null : "Campo obrigatório";
}

function validateRegex(input, regex, errorMsg = "Campo inválido") {
    return regex.test(input.value) ? null : errorMsg;
}

function validatePassword(input) {
    const haveNumber = /\d/.test(input.value);
    const haveLetter = /[a-zA-Z]/.test(input.value);
    const haveSpecialChar = /[^a-zA-Z\d]/.test(input.value);
    const haveLengthEightOrMore = input.value.length >= 8;
    return !haveNumber || !haveLetter || !haveSpecialChar || !haveLengthEightOrMore ? "Senha inválida" : null;
}

function validateConfirmPassword(input, errorMsg = "Senhas não conferem") {
    return input.value === passwordInput.value ? null : errorMsg;
}

function validateDate(input, errorMsg = "Data inválida") {
    const now = new Date();
    const date = new Date(input.value);
    const isInvalid =
        isNaN(date.getTime()) ||
        date > now ||
        date.getFullYear() < 1900 ||
        date.getFullYear() > now.getFullYear() - 18;
    return isInvalid ? errorMsg : null;
}


function reloadErrorMaps(inputs) {
    inputs.forEach(input => {
        if (nonRequiredInputs.includes(input)) {
            return;
        }
        globalErrors.set(input.id, "Campo obrigatório");
    });
}


export function showErrorForInput(input) {
    input.focus();
    input.setCustomValidity(globalErrors.get(input.id));
    input.reportValidity();
}

function checkIfSectionCardsIsProvided() {
    const cardsDivs = document.querySelectorAll(".cards");
    for (const div of cardsDivs) {
        if (div.children.length < 1) {
            return false;
        }
    }
    return true;
}

function verifyIfIsInsertingSectionInfo() {
    let result = false;
    for (const value of controlInsertMap.values()) {
        result ||= value;
    }
    return result;
}


function updateSubmitButton() {
    if (globalErrors.size === 0 &&
        checkIfSectionCardsIsProvided() &&
        !verifyIfIsInsertingSectionInfo()) {
        submitButton.removeAttribute("disabled");
        submitButton.classList.remove("disabled");
    } else {
        submitButton.setAttribute("disabled", "disabled");
        submitButton.classList.add("disabled");
    }
}


function attachValidation(input, validatorFn, extraSetup) {
    const validateHandler = () => {
        const error = validatorFn(input);
        if (error) {
            globalErrors.set(input.id, error);
        } else {
            globalErrors.delete(input.id);
        }
        if (extraSetup) {
            extraSetup();
        }
    };

    input.addEventListener("input", validateHandler);
    input.addEventListener("change", () => {
        input.setCustomValidity("");
        if (globalErrors.has(input.id)) {
            showErrorForInput(input);
        }
    });
}

attachValidation(nameInput, validateEmptyness);

attachValidation(cpfInput, input =>
    validateRegex(input, /^\d{3}\.\d{3}\.\d{3}-\d{2}$/, "CPF inválido")
);

attachValidation(phoneInput, input =>
    validateRegex(input, /^\(\d{2}\) \d{4,5}-\d{4}$/, "Telefone inválido")
);

attachValidation(birthdateInput, input =>
    validateDate(input, "Data inválida, lembre-se que a idade mínima é 18 anos")
);

attachValidation(phoneTypeInput, validateEmptyness);

attachValidation(genreInput, validateEmptyness);

attachValidation(deliveryAddressName, validateEmptyness,
    () => setupValidationForSectionInputs(deliveryAddressSection, deliveryAddresses, globalErrors, "delivery", deliveryAddressCrucialInputs)
);

attachValidation(deliveryCepInput, input =>
    validateRegex(input, /^\d{5}-\d{3}$/, "CEP inválido"),
    () => setupValidationForSectionInputs(deliveryAddressSection, deliveryAddresses, globalErrors, "delivery", deliveryAddressCrucialInputs)

);

attachValidation(deliveryNumberInput, input =>
    validateRegex(input, /^\d+$/, "O campo deve ser um número"),
    () => setupValidationForSectionInputs(deliveryAddressSection, deliveryAddresses, globalErrors, "delivery", deliveryAddressCrucialInputs)

);

attachValidation(deliveryNeighborhoodInput, validateEmptyness,
    () => setupValidationForSectionInputs(deliveryAddressSection, deliveryAddresses, globalErrors, "delivery", deliveryAddressCrucialInputs)

);

attachValidation(deliveryCityInput, validateEmptyness,
    () => setupValidationForSectionInputs(deliveryAddressSection, deliveryAddresses, globalErrors, "delivery", deliveryAddressCrucialInputs)

);

attachValidation(deliveryStateInput, validateEmptyness,
    () => setupValidationForSectionInputs(deliveryAddressSection, deliveryAddresses, globalErrors, "delivery", deliveryAddressCrucialInputs)

);

attachValidation(deliveryCountryInput, validateEmptyness,
    () => setupValidationForSectionInputs(deliveryAddressSection, deliveryAddresses, globalErrors, "delivery", deliveryAddressCrucialInputs)

);

attachValidation(deliveryAddressInput, validateEmptyness,
    () => setupValidationForSectionInputs(deliveryAddressSection, deliveryAddresses, globalErrors, "delivery", deliveryAddressCrucialInputs)

);

attachValidation(deliveryAddressType, validateEmptyness,
    () => setupValidationForSectionInputs(deliveryAddressSection, deliveryAddresses, globalErrors, "delivery", deliveryAddressCrucialInputs)

);

attachValidation(emailInput, input =>
    validateRegex(input, /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/, "Email inválido")
);

attachValidation(cardNumberInput, input =>
    validateRegex(input, /^\d{4} \d{4} \d{4} \d{4}$/, "Número de cartão inválido"),
    () => setupValidationForSectionInputs(cardSection, creditCards, globalErrors, "credit_card", creditCardCrucialFields)
);

attachValidation(cardNameInput, input => validateRegex(input, /^[a-zA-Z ]+$/, "Nome inválido"),
    () => setupValidationForSectionInputs(cardSection, creditCards, globalErrors, "credit_card", creditCardCrucialFields)
);

attachValidation(cardBrandInput, validateEmptyness,
    () => setupValidationForSectionInputs(cardSection, creditCards, globalErrors, "credit_card", creditCardCrucialFields)
);

cardNameInput.addEventListener("input", () => {
    cardNameInput.value = cardNameInput.value.toUpperCase();
});

attachValidation(cardVerificationCodeInput, input =>
    validateRegex(input, /^\d{3}$/, "Código inválido"),
    () => setupValidationForSectionInputs(cardSection, creditCards, globalErrors, "credit_card", creditCardCrucialFields)
);

attachValidation(passwordInput, validatePassword);

attachValidation(passwordConfirmationInput, validateConfirmPassword);


deliveryAddressObservationsInput.addEventListener("input", () => {});

const allInputs = document.querySelectorAll("form#main input");
reloadErrorMaps(allInputs);
allInputs.forEach(input =>
    input.addEventListener("input", () => {
        input.setCustomValidity("");
        updateSubmitButton();
    })
);


setupButtonInSection(deliveryAddressSection, deliveryAddresses, "Endereços", "delivery", () => {
    controlInsertMap.set("delivery", false);
    updateSubmitButton();
});

setupButtonInSection(cardSection, creditCards, "Cartão de crédito", "credit_card", () => {
    controlInsertMap.set("credit_card", false);
    updateSubmitButton();
});

function showInputElements(inputs) {
    inputs.forEach(input => {
        input.style.display = "block";
        const label = document.querySelector(`label[for="${input.id}"]`);
        if (label) {
            label.style.display = "block";
        }
    });
}

function hideInputElements(inputs) {
    inputs.forEach(input => {
        input.value = "";
        input.style.display = "none";
        const label = document.querySelector(`label[for="${input.id}"]`);
        if (label) {
            label.style.display = "none";
        }
    });
}

function createAddAddressButton(section, id = "") {
    const addButton = document.createElement("button");
    addButton.textContent = `Adicionar ${controlTitleMap.get(id)}`;
    addButton.addEventListener("click", () => handleAddAddresses(addButton, section, id));
    return addButton;
}

function createCancelButton(inputs, addButton, section, id = "") {
    const cancelButton = document.createElement("button");
    cancelButton.textContent = "Cancelar";
    cancelButton.addEventListener("click", () => {
        hideInputElements(inputs);
        inputs.forEach(input => {
            globalErrors.delete(input.id);
        });
        controlInsertMap.set(id, false);
        const button = section.querySelector(`button${id ? `#${id}` : ""}`);
        button.style.display = "none";
        cancelButton.remove();
        section.appendChild(addButton);
        updateSubmitButton();
    });
    return cancelButton;
}

function handleAddAddresses(addButton, section, id = "") {
    controlInsertMap.set(id, true);
    const inputs = Array.from(section.querySelectorAll("input, select, textarea"));
    showInputElements(inputs);
    const firstLabel = document.querySelector(`label[for="${inputs[0].id}"]`);
    const cancelButton = createCancelButton(inputs, addButton, section, id);
    section.insertBefore(cancelButton, firstLabel);
    reloadErrorMaps(inputs);

    const submitButton = section.querySelector(`button${id ? `#${id}` : ""}`);
    submitButton.style.display = "block";
    submitButton.setAttribute("disabled", "disabled");
    submitButton.classList.add("disabled");
    const mainButton = document.querySelector("button[type='submit']");
    mainButton.setAttribute("disabled", "disabled");
    mainButton.classList.add("disabled");

    const removeCancel = () => {
        cancelButton.remove();
        section.appendChild(addButton);
        submitButton.removeEventListener("click", removeCancel);

    };
    submitButton.addEventListener("click", removeCancel);


    addButton.remove();
}

deliveryAddressSection.addEventListener("focus", () => {
    const deliveryBtn = deliveryAddressSection.querySelector("button#delivery");
    const buttons = deliveryAddressSection.querySelectorAll("button");
    if (deliveryBtn.style.display === "none" && buttons.length === 1) {
        deliveryAddressSection.appendChild(createAddAddressButton(deliveryAddressSection, "delivery"));
    }
});



cardSection.addEventListener("focus", () => {
    const cardBtn = cardSection.querySelector("button#credit_card");
    const buttons = cardSection.querySelectorAll("button");

    if (cardBtn.style.display === "none" && buttons.length === 1) {
        cardSection.appendChild(createAddAddressButton(cardSection, "credit_card"));
    }
});