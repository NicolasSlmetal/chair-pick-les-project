import { passwordInput, passwordConfirmationInput, form } from '../consts.js'
import { submitButton } from '../consts.js'
import { updatePassword } from './updatePassword.js'


const email = document.querySelector("input#email").value;

form.addEventListener("submit", async (event) => {
    event.preventDefault();
    const data = Object.fromEntries(new FormData(form));
    delete data["password_confirmation"];
    data.email = email;
    await updatePassword(data);
});

const globalErrors = new Map();


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

export function showErrorForInput(input) {
    input.focus();
    input.setCustomValidity(globalErrors.get(input.id));
    input.reportValidity();
}

function reloadErrorMaps(inputs) {
    inputs.forEach(input => {
        if (input.type === "hidden") {
            return;
        }

        globalErrors.set(input.id, "Campo obrigatório");
    });
}

function updateSubmitButton() {
    console.log(globalErrors);
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

attachValidation(passwordInput, validatePassword);

attachValidation(passwordConfirmationInput, validateConfirmPassword);

const allInputs = document.querySelectorAll("form#main input");
reloadErrorMaps(allInputs);
allInputs.forEach(input =>
    input.addEventListener("input", () => {
        input.setCustomValidity("");
        updateSubmitButton();
    })
);