import {
amountInput,
costInput,
supplierInput,
entryDateInput,
submitButton} from "./consts.js";
import { createItem } from "./createItem.js";

const $ = window.jQuery;
const form = document.querySelector("form#main");
form.addEventListener("submit", async (event) => {
    event.preventDefault();
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());
    const supplier = $(`#${supplierInput.id}`).select2("data");
    data.supplier = {}
    data.supplier.id = supplier.length > 0 ? supplier[0].id : null;
    if (isNaN(data.supplier.id)) {
       data.supplier.id  = null;
    }

    data.supplier.name = supplier.length > 0 ? supplier[0].text : null;
    data.amount = Number(data.amount);
    data.unitCost = Number(data.cost);
    data.entryDate = data.entry_date;
    delete data.entry_date;

    await createItem(data);
});

$(document).ready(() => {
    $("#supplier").select2({
        tags: true,
        width: "100%",
        placeholder: "Selecione ou adicione um fornecedor",
        ajax: {
            url: "/admin/suppliers",
            dataType: "json",
            processResults: function (data) {
                return {
                    results: data.map(supplier => ({
                        id: supplier.id,
                        text: supplier.name
                    }))
                };
            }

        }

    })

    $("#entry_date").val(new Date().toISOString().split("T")[0]);
    document.querySelector("#entry_date").dispatchEvent(new Event("input"));
});

const globalErrors = new Map();

function validateValidNumber(input) {
    return isNaN(input.value) || input.value.trim() === "" ? "Por favor, insira um número válido." : "";
}

function validateNotEmpty(input) {

    return input.value.trim() === "" ? "Este campo não pode estar vazio." : "";
}

function validateEntryDate(input) {
    const value = new Date(input.value);
    const today = new Date();
    if (value > today) {
        return "A data de entrada não pode ser no futuro.";
    }

    return "";
}

function loadGlobalErrors() {
    document.querySelectorAll("input, select, textarea").forEach(input => {
        if (input.value.trim() !== "") {
             return;
        }
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

loadGlobalErrors();
attachValidation(amountInput, validateValidNumber, () => {
    amountInput.value = amountInput.value.trim().replace(/[^0-9]/g, "");
});
attachValidation(costInput, validateValidNumber);
attachValidation(supplierInput, validateNotEmpty);
attachValidation(entryDateInput, validateEntryDate);

const allInputs = document.querySelectorAll("input, select, textarea");
allInputs.forEach(input =>
    input.addEventListener("input", () => {

        input.setCustomValidity("");
        updateSubmitButton();
    })
);

$(`#${supplierInput.id}`).on("change", (event) => {

    const selected = $(`#${supplierInput.id}`).select2("data");
    if (selected === null || selected.length === 0) {
        globalErrors.set(supplierInput.id, "Selecione ou adicione um fornecedor.");
        showErrorForInput(supplierInput);
        updateSubmitButton();
        return;
    }
    globalErrors.delete(supplierInput.id);
    supplierInput.setCustomValidity("");
    updateSubmitButton();
});

