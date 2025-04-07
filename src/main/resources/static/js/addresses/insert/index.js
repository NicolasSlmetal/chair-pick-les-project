import { nameInput, cepInput, numberInput, neighborhoodInput, addressObservationsInput, cityInput, stateInput, countryInput, addressInput, addressType } from "../consts.js";
import { form, submitButton } from "../../consts.js";
import { postAddress } from "./postAddress.js";

const backButton = document.querySelector("#back-button");

backButton.addEventListener("click", () => {
    const lastActivity = window.history;
    if (lastActivity) {
        lastActivity.back();
        return;
    }
    window.location.href = "/";
});

form.addEventListener("submit", async (event) => {
    event.preventDefault();
    const data = Object.fromEntries(new FormData(form));
    data["observations"] = data["observations"] || "";
    data["streetType"] = data["address_type"].toUpperCase();
    data["isDefault"] = data["default"] ? true : false;
    data["street"] = data["address"];
    console.log({data});
    await postAddress(data);
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

        if (!input.id || input.id === "default" || input.id === "customerId" || input.type === "select" || input.id === "observations") {
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
    console.log({globalErrors});
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

attachValidation(nameInput, validateEmptyness);
attachValidation(cepInput, input => validateRegex(input, /^\d{5}-\d{3}$/, "CEP inválido"));
attachValidation(numberInput, validateEmptyness);
attachValidation(neighborhoodInput, validateEmptyness);
attachValidation(cityInput, validateEmptyness);
attachValidation(stateInput, validateEmptyness);
attachValidation(countryInput, validateEmptyness);
attachValidation(addressInput, validateEmptyness);
attachValidation(addressType, validateEmptyness);

const allInputs = document.querySelectorAll("form#main input");
reloadErrorMaps(allInputs);
allInputs.forEach(input =>
    input.addEventListener("input", () => {
        input.setCustomValidity("");
        updateSubmitButton();
    })
);