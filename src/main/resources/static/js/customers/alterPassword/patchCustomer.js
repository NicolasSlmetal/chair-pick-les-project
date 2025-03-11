import { patch } from "../../http/api.js";
import { parseErrorMessages } from "../../utils/errorMessage.js";

const dialog = document.querySelector("dialog");
const button = dialog.querySelector("button");
button.addEventListener("click", () => {
    dialog.close()
});

export async function patchCustomer(customer) {
    const response = await patch(`customers/${customer.id}/alter-password`, customer);
    if (response.status !== 200) {
        const errorJson = await response.json();
        const p = dialog.querySelector("p");
        p.innerText = parseErrorMessages(errorJson.message);
        dialog.showModal();
        return;
    }

    window.location.href = "/customers";
}