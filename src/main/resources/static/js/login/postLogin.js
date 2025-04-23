import { post } from "../http/api.js";
import { parseErrorMessages } from "../utils/errorMessage.js";

const errorDialog = document.querySelector("dialog#error__modal");
const button = errorDialog.querySelector("button");
button.addEventListener("click", () => {
    errorDialog.close();
});

export async function postLogin(data) {
    const body = await post("login", data, 200);
    if (body.status !== 200) {
        const p = errorDialog.querySelector("p");
        const messages = await body.json();
        const errorMessages = parseErrorMessages(messages.message);
        p.innerText = errorMessages;
        errorDialog.showModal();
        return;
    }

    const json = await body.json();
    if (json.role == "ADMIN") {
        window.location.href = "/admin/customers";
        return;
    }

    const lastActivity = window.history;
    if (!lastActivity) {
        window.location.href = "/";
        return;
    }

    lastActivity.back();

}