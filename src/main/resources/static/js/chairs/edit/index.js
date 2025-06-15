import { configureDynamicInputs } from "../new/configureDynamicInputs.js";
import {
    chairNameInput,
    chairDescriptionInput,
    chairCategoriesInput,
    chairLengthInput,
    chairWidthInput,
    chairHeightInput,
    chairWeightInput,
    chairPricingGroupInput,
    chairAverageRatingInput,
    chairImageInput,
    priceInput,
    priceChangeReasonInput,
    selectChangePrice,
    submitButton
} from './consts.js'
import { updateChair } from "./updateChair.js";
const form = document.querySelector("form#main");

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const data = getData();

    data.pricingGroupId = data.pricing_group;

    const formData = new FormData();
    formData.append("image", data.image);
    delete data.image;
    formData.append("input", new Blob([JSON.stringify(data)], { type: "application/json" }));
    await updateChair(data.id, formData);
});

const globalErrors = new Map();
configureDynamicInputs();
const beforeUpdateData = getData();
function getData() {
    const formData = new FormData(form);
    const data = {};
    formData.forEach((value, key) => {
        if (key === "categories" || key === "pricing_group" || key === "image") {
            data[key] = $(`#${key}`).val();
            return;
        }


        data[key] = value;

    });
    return data;
}

function validateNotEmpty(input) {

    return input.value.trim() === "" ? "Este campo não pode estar vazio." : "";
}

function validateValidNumber(input) {
    return isNaN(input.value) || input.value.trim() === "" ? "Por favor, insira um número válido." : "";
}

function validateAverageRating(input) {
    const value = parseFloat(input.value);
    if (isNaN(value) || value < 0 || value > 5) {
        return "A média de avaliações deve ser um número entre 0 e 5.";
    }
    return "";
}

function validatePrice(input) {
    const isStatusChanged = selectChangePrice.value;
    if (isStatusChanged === "no") {
        return "";
    }
    const validNumberMessages = validateValidNumber(input);
    if (validNumberMessages) {
        return validNumberMessages;
    }

    const beforeUpdateDataValue = beforeUpdateData.price;
    const currentValue = parseFloat(input.value);

    if (currentValue <= 0) {
        return "O preço deve ser maior que zero.";
    }

    if (currentValue === beforeUpdateDataValue) {
        return "O preço deve ser diferente do valor atual.";
    }

    return "";

}

function validatePriceChangeReason(input) {
    const isStatusChanged = selectChangePrice.value;
    if (isStatusChanged === "no") {
        return "";
    }
    return validateNotEmpty(input);
}

function attachValidation(input, validatorFn, extraSetup) {
    const validateHandler = () => {

        const error = validatorFn(input);
        if (error) {
            globalErrors.set(input.id, error);
            updateSubmitButton();
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

function showErrorForInput(input) {
    input.focus();
    input.setCustomValidity(globalErrors.get(input.id));
    input.reportValidity();
}

function updateSubmitButton() {
    const currentData = getData();

    if (globalErrors.size === 0 && JSON.stringify(beforeUpdateData) != JSON.stringify(currentData)) {
        submitButton.removeAttribute("disabled");
        submitButton.classList.remove("disabled");
    } else {
        submitButton.setAttribute("disabled", "disabled");
        submitButton.classList.add("disabled");
    }
}

attachValidation(chairNameInput, validateNotEmpty);
attachValidation(chairDescriptionInput, validateNotEmpty);
attachValidation(chairCategoriesInput, validateNotEmpty);
attachValidation(chairLengthInput, validateValidNumber);
attachValidation(chairWidthInput, validateValidNumber);
attachValidation(chairHeightInput, validateValidNumber);
attachValidation(chairWeightInput, validateValidNumber);
attachValidation(chairPricingGroupInput, validateNotEmpty);
attachValidation(chairAverageRatingInput, validateAverageRating);
attachValidation(chairImageInput, validateNotEmpty);
attachValidation(priceInput, validatePrice);
attachValidation(priceChangeReasonInput, validatePriceChangeReason);

selectChangePrice.addEventListener("input", () => {
    const isStatusChanged = selectChangePrice.value === "yes";

    if (isStatusChanged) {

        const reason = priceChangeReasonInput.value.trim();
        if (reason === "") {
            globalErrors.set(priceChangeReasonInput.id, "Por favor, informe o motivo da alteração do preço.");
            showErrorForInput(priceChangeReasonInput);
        }
    } else {
        globalErrors.delete(priceChangeReasonInput.id);
        globalErrors.delete(priceInput.id);
        priceChangeReasonInput.value = "";
        priceInput.value = beforeUpdateData.price;
    }

});

const allInputs = document.querySelectorAll("form#main input, select, textarea");

allInputs.forEach(input =>
    input.addEventListener("input", () => {

        input.setCustomValidity("");
        updateSubmitButton();
    })
);

chairImageInput.addEventListener("input", () => {
    const label = document.querySelector("label[for='image']");
    if (chairImageInput.files.length > 0) {
        label.innerText = chairImageInput.files[0].name;
    } else {
        label.innerText = "Selecione uma imagem";
    }
    updateSubmitButton();
});

$("#categories").on("change", () => configureDynamicInput("categories")).on("select2:unselect", () => configureDynamicInput("categories"));

$("#pricing_group").on("change", () => configureDynamicInput("pricing_group")).on("select2:unselect", () => configureDynamicInput("pricing_group"))

function configureDynamicInput(inputId) {
    const value = $(`#${inputId}`).val();
    if (value) {
        globalErrors.delete(inputId);
    } else {
        globalErrors.set(inputId, "Este campo é obrigatório.");
    }
    updateSubmitButton();
}
