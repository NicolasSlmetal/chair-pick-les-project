import { configureDynamicInputs } from './configureDynamicInputs.js';
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
    submitButton
} from './consts.js'
import { $ } from "../../consts.js";

import { createChair } from "./createChair.js";

const form = document.querySelector("form#main");

form.addEventListener("submit", async (event) => {
    event.preventDefault();
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());
    const categories = $("#categories").select2("data").map(category => Number(category.id));
    data.categories = categories;
    const [ pricingGroup ] = $("#pricing_group").select2("data").map(group => Number(group.id));
    data.pricingGroupId = pricingGroup;

    data.averageRating = data.rating;
    delete data.rating;

    const postData = new FormData();
    postData.append("image", data.image);
    delete data.image;

    postData.append("input", new Blob([JSON.stringify(data)], { type: "application/json" }));

    await createChair(postData);
})



const globalErrors = new Map();

configureDynamicInputs();

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


function loadGlobalErrors() {
    document.querySelectorAll("input, select, textarea").forEach(input => {
        globalErrors.set(input.id, "Campo obrigatório.");
    });
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
    if (globalErrors.size === 0) {
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

const allInputs = document.querySelectorAll("form#main input, select, textarea");
loadGlobalErrors();
allInputs.forEach(input =>
    input.addEventListener("input", () => {

        input.setCustomValidity("");
        updateSubmitButton();
    })
);


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


