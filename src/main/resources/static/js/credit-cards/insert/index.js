import { cardNumberInput, cardNameInput, cardVerificationCodeInput, cardBrandInput, submitButton, form } from "../../consts.js";
import { postCreditCard } from "./postCreditCard.js";

const backButton = document.querySelector("#back_button");

backButton.addEventListener("click", () => {
    const lastActivity = window.history;
    if (lastActivity) {
        lastActivity.back();
        return;
    }
    window.location.href = `/`;
});
form.addEventListener("submit", async (event) => {
    event.preventDefault();
    const data = Object.fromEntries(new FormData(form));
    Object.keys(data).forEach(key => {
      data[key.replace("card_", "")] = data[key];
    });
    data["isDefault"] = data["default"] ? true : false;
    data["brand"] = data["brand"].toUpperCase();
    await postCreditCard(data);
});

const globalErrors = new Map();

function validateEmptyness(input) {
    return input.value ? null : "Campo obrigatório";
}

function validateRegex(input, regex, errorMsg = "Campo inválido") {
    return regex.test(input.value) ? null : errorMsg;
}

function reloadErrorMaps(inputs) {
    inputs.forEach(input => {

        if (!input.id || input.id === "default" || input.id === "customer_id") {
            return;
        }
        globalErrors.set(input.id, null);
    });
}

function showErrorForInput(input) {
    input.focus();
    input.setCustomValidity(globalErrors.get(input.id));
    input.reportValidity();
}

function updateSubmitButton() {

    if (globalErrors.size === 0) {
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

attachValidation(cardNumberInput, input => validateRegex(input, /^\d{4} \d{4} \d{4} \d{4}$/, "Número inválido"));

attachValidation(cardNameInput, input => validateRegex(input, /^[a-zA-Z ]+$/, "Nome inválido"));

attachValidation(cardBrandInput, validateEmptyness);

cardNameInput.addEventListener("input", () => {
    cardNameInput.value = cardNameInput.value.toUpperCase();
});

attachValidation(cardVerificationCodeInput, input =>
    validateRegex(input, /^\d{3}$/, "Código inválido"));

const allInputs = document.querySelectorAll("form#main input");
reloadErrorMaps(allInputs);
allInputs.forEach(input =>
    input.addEventListener("input", () => {
        input.setCustomValidity("");
        updateSubmitButton();
    })
);