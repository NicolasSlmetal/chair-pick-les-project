import { postLogin } from "./postLogin.js";
import { postRequestResetPassword } from "./postRequestResetPassword.js";
import { parseErrorMessages } from "../utils/errorMessage.js";

const email = document.getElementById('email');
const password = document.getElementById('password');

const submitButton = document.querySelector('button[type="submit"]');
const forgetPasswordButton = document.querySelector('button#forget');
const forgetPasswordDialog = document.querySelector('dialog#forget__modal');
const resultModal = document.querySelector('dialog#result__modal');
const okResultModal = resultModal.querySelector('button');
okResultModal.addEventListener("click", () => {
    resultModal.close();
    const input = forgetPasswordDialog.querySelector("input");
    input.value = "";
});

function configureCancelButton(dialog) {
    const cancelButton = dialog.querySelector("button#dialog__cancel__button");
    cancelButton.addEventListener("click", () => {
        dialog.close();
    });
}

function configureConfirmButton(dialog, action) {
    const confirmButton = dialog.querySelector("button#dialog__confirm__button");
    confirmButton.addEventListener("click", action);
}

configureCancelButton(forgetPasswordDialog);
configureConfirmButton(forgetPasswordDialog, async () => {
    forgetPasswordDialog.close();
    const input = forgetPasswordDialog.querySelector("input");
    const emailValue = input.value.trim();
    input.value = "";
    const response = await postRequestResetPassword(emailValue);
    const pStatus = resultModal.querySelector("p");
    if (response.status === 200) {
        pStatus.innerText = "Se o e-mail estiver cadastrado, você receberá um link para redefinir sua senha.";
    }
    if (response.status === 400) {
        const messages = await response.text();

        if (messages === "A reset password is still in progress in this device") {
            pStatus.innerText = "Um pedido de redefinição de senha já está em progresso"
        } else {
            pStatus.innerText = parseErrorMessages(messages);

        }
    }
    resultModal.showModal();
});


forgetPasswordButton.addEventListener("click", () => {
    forgetPasswordDialog.showModal();
});

const globalErrors = new Map();

const form = document.querySelector("form");
form.addEventListener("submit", async (event) => {
    event.preventDefault();
    if (event.submitter === submitButton) {
        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());
        await postLogin(data);
        return;
    }
});

function updateSubmitButton() {
    if (globalErrors.size === 0) {
        submitButton.removeAttribute("disabled");
        submitButton.classList.remove("disabled");
    } else {
        submitButton.setAttribute("disabled", "disabled");
        submitButton.classList.add("disabled");
    }
}

function validateRegex(input, regex, errorMsg = "Campo inválido") {
    return regex.test(input.value) ? null : errorMsg;
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

function validatePassword(input) {
    const haveNumber = /\d/.test(input.value);
    const haveLetter = /[a-zA-Z]/.test(input.value);
    const haveSpecialChar = /[^a-zA-Z\d]/.test(input.value);
    const haveLengthEightOrMore = input.value.length >= 8;
    return !haveNumber || !haveLetter || !haveSpecialChar || !haveLengthEightOrMore ? "Senha inválida" : null;
}

attachValidation(email, (input) => validateRegex(input, /^[a-z0-9.]+@[a-z0-9]+\.[a-z]+$/i, "E-mail inválido"));
attachValidation(password, validatePassword);

function showErrorForInput(input) {
    input.setCustomValidity(globalErrors.get(input.id));
    input.reportValidity();
}

if (validateRegex(email, /^[a-z0-9.]+@[a-z0-9]+\.[a-z]+$/i) !== null) {
    globalErrors.set(email.id, "E-mail inválido");
}

if (validatePassword(password) !== null) {
    globalErrors.set(password.id, "Senha inválida");
}

if (globalErrors.size == 0) {
    submitButton.removeAttribute("disabled");
    submitButton.classList.remove("disabled");
}

const inputs = [email, password];
inputs.forEach(input => {
    input.addEventListener("input", () => {
        updateSubmitButton();
    });
});
