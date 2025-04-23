import { updateOrderItemStatus } from './updateOrderItem.js';

const orderId = document.querySelector("#orderId").value;
const dialog = document.querySelector("#confirm__modal");
const dialogTitle = dialog.querySelector("h2");
const dialogMessage = dialog.querySelector("p");
const confirmDialogButton = dialog.querySelector("#confirm__button");
const cancelDialogButton = dialog.querySelector("#cancel__button");
cancelDialogButton.addEventListener("click", function () {
    dialog.close();
});


const errorDialog = document.querySelector("#error__modal");
const errorDialogMessage = errorDialog.querySelector("p");
const errorDialogButton = errorDialog.querySelector("button");
errorDialogButton.addEventListener("click", function () {
    errorDialog.close();
});

const dispatchButtons = document.querySelectorAll(".dispatch");
const deliveredButtons = document.querySelectorAll(".delivered");
var selectedOrderItemId = undefined;
var orderItemStatus = undefined;

confirmDialogButton.addEventListener("click", async () => {
    dialog.close();

    if (selectedOrderItemId == undefined || orderItemStatus == undefined) {
        return;
    }

    const response = await updateOrderItemStatus(orderId, selectedOrderItemId, orderItemStatus);

    if (response.status != 200) {
        const errors = await response.json();
        const errorMessages = parseErrorMessages(errors.message);
        errorDialogMessage.innerHTML = errorMessages;
        errorDialog.showModal();
        return;
    }

    window.location.reload();
});

dispatchButtons.forEach(button => {
    button.addEventListener("click", () => {
        const itemName = button.parentElement.parentElement.querySelector("td:nth-child(1)").innerText;
        selectedOrderItemId = button.parentElement.querySelector("input[name='orderItemId']").value;
        renderModal("Despachar item", "Tem certeza que deseja despachar o item " + itemName + "?", "DELIVERING");
    });
});

deliveredButtons.forEach(button => {
    button.addEventListener("click", () => {
        const itemName = button.parentElement.parentElement.querySelector("tr:nth-child(1) td").innerText;
        selectedOrderItemId = button.parentElement.querySelector("input[name='orderItemId']").value;
        renderModal("Entregar pedido", "Tem certeza que deseja entregar o pedido " + itemName + "?", "DELIVERED");
    });
});

function renderModal(title, message, status) {
    orderItemStatus = status;
    dialog.showModal();
    dialogMessage.innerHTML = message;
    dialogTitle.innerText = title;
}

