import { post } from "../../http/api.js";
import { parseErrorMessages } from "../../utils/errorMessage.js";

const dialog = document.querySelector("dialog");
const button = dialog.querySelector("button");
button.addEventListener("click", () => {
    dialog.close()
});

export async function postCustomer(customer) {
    const response = await post("customers", customer);

    if (response.status !== 201) {
        const p = dialog.querySelector("p");
        const errorJson = await response.json();
        p.innerText = parseErrorMessages(errorJson.message);
        dialog.showModal();

        return;
    }
    const lastActivity = window.history;
    if (lastActivity) {
        lastActivity.back();
        return;
    }
    window.location.href = "/";


}