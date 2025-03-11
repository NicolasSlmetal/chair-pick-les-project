import {  birthdateInput, cpfInput, emailInput, form, nameInput, phoneInput, submitButton } from "../../consts.js";
import { putCustomer } from "./putCustomer.js";

const originalValue = Object.fromEntries(new FormData(form));

form.addEventListener("submit", async (event) => {
    event.preventDefault();
    const formData = new FormData(form);
    const data = Object.fromEntries(formData);
    data["phoneType"] = data["phone_type"];
    data["birthDate"] = data["birthdate"];

    await putCustomer(data);
});
const inputs = [...document.querySelectorAll("input"), ...document.querySelectorAll("select"), ...document.querySelectorAll("textarea")].flat();

const errors = new Map();

function checkValidationInputForEmptyness(input) {
  if (!input.value) {
    errors.set(input.id, "Campo obrigatório");
  } else  {
    errors.delete(input.id);
  }
}

function isEqualOriginal() {
    const data = Object.fromEntries(new FormData(form));
    for (const key in data) {
        if (typeof data[key] == "string") {
            data[key] = data[key].trim();
        }
    }
    return JSON.stringify(data) === JSON.stringify(originalValue);
}

function checkValidationInputForRegex(input, regex, customErrorMessage = "Campo inválido") {
  if (!regex.test(input.value.trim())) {
    errors.set(input.id, customErrorMessage);
  } else {
    errors.delete(input.id);
  }
}

function checkValidationInputForDate(input, customErrorMessage = "Data inválida") {
    const now = new Date();
    const date = new Date(input.value);
    const isInvalid = isNaN(date.getTime()) || date > now || date.getFullYear() < 1900 || date.getFullYear() > now.getFullYear() - 18;

    if (isInvalid) {
        errors.set(input.id, customErrorMessage);

    } else {
        errors.delete(input.id);
    }
}

nameInput.addEventListener("input", () => {
    checkValidationInputForEmptyness(nameInput);
});

cpfInput.addEventListener("input", () => {

    checkValidationInputForRegex(cpfInput, /^\d{3}\.\d{3}\.\d{3}-\d{2}$/ , "CPF inválido");
});

phoneInput.addEventListener("input", () => {
    checkValidationInputForRegex(phoneInput, /^\(\d{2}\) \d{4,5}-\d{4}$/ , "Telefone inválido");
});

birthdateInput.addEventListener("input", () => {
    checkValidationInputForDate(birthdateInput, "Data inválida, lembre-se que a idade mínima é 18 anos");
});

emailInput.addEventListener("input", () => {
    checkValidationInputForRegex(emailInput, /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/ , "Email inválido");
});

function showErrorForInput(input) {
    input.focus();
    input.setCustomValidity(errors.get(input.id));
    input.reportValidity();
}


inputs.forEach(input => {

    input.addEventListener("change", () => {

        input.setCustomValidity("");

        if (errors.has(input.id)) {
            showErrorForInput(input);
        }

    });

    input.addEventListener("input", () => {
        input.setCustomValidity("");
        if (errors.size === 0 && !isEqualOriginal()) {
            submitButton.removeAttribute("disabled");
            submitButton.classList.remove("disabled");
        } else {
            submitButton.setAttribute("disabled", "disabled");
            submitButton.classList.add("disabled");
        }
    })
});

