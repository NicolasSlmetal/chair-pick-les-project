import { updatePriceChangeRequest } from './updatePriceChangeRequest.js';

const dialog = document.querySelector("#confirm_action");
const cancelButton = dialog.querySelector("#cancel_button");
cancelButton.onclick = () => {
    dialog.close();
}

const chairId = document.querySelector("input[name='chairId']").value;

const confirmButton = dialog.querySelector("#confirm_button");

const acceptButtons = document.querySelectorAll("button.accept");
const rejectButtons = document.querySelectorAll("button.reject");

acceptButtons.forEach((button) => {
    button.addEventListener("click", () => {
        const row = button.closest("tr");
        const requestPriceId = row.querySelector("input[name='request_price_id']").value;
        const action = async () => {
            dialog.close();
            await updatePriceChangeRequest(requestPriceId, chairId, "APPROVED")
        }
        const p = dialog.querySelector("p");
        p.innerText = `Tem certeza que deseja aceitar a alteração de preço?`;
        dialog.showModal();
        confirmButton.onclick = action;

    });
});

rejectButtons.forEach((button) => {
    button.addEventListener("click", () => {
        const row = button.closest("tr");

        const requestPriceId = row.querySelector("input[name='request_price_id']").value;
        const action = async () => {
            dialog.close();
            await updatePriceChangeRequest(requestPriceId, chairId, "REPROVED")
        }
        const p = dialog.querySelector("p");
        p.innerText = `Tem certeza que deseja rejeitar a alteração de preço?`;
        dialog.showModal();
        confirmButton.onclick = action;

    });
})