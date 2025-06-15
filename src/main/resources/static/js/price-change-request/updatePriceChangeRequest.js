import { patch } from "../http/api.js";
import { parseErrorMessages } from "../utils/errorMessage.js";

const errorDialog = document.querySelector("dialog#error__modal");
const button = errorDialog.querySelector("button");
button.addEventListener("click", () => {
    errorDialog.close();
});

export async function updatePriceChangeRequest(id, chairId, status) {
    const response = await patch(`admin/chairs/${chairId}/price-change-requests/${id}`, status);

    if (response.status != 200) {

        const p = errorDialog.querySelector("p");
        const messages = await response.json();
        p.innerText = parseErrorMessages(messages.message);
        errorDialog.showModal();
        return;
    }

    window.location.reload();
}