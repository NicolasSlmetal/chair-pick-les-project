import { updatePayment} from './updatePayment.js';
import { parseErrorMessages } from '../../utils/errorMessage.js'

const dialog = document.querySelector("#confirm__dialog");
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

const rejectButton = document.querySelector("#reject");
const approveButton = document.querySelector("#approve");

if (rejectButton) {
    rejectButton.addEventListener("click", () => {
        renderModal("Reprovar pagamento", "Tem certeza que deseja rejeitar o pagamento?", "REPROVED");
    });
}

if (approveButton) {
    approveButton.addEventListener("click", () => {
        renderModal("Aprovar pagamento", "Tem certeza que deseja aprovar o pagamento?", "APPROVED");
    });
}



var orderStatus = undefined;
function renderModal(title, message, status) {
    orderStatus = status;
    dialog.showModal();
    dialogMessage.innerHTML = message;
    dialogTitle.innerText = title;
}

confirmDialogButton.addEventListener("click", async () => {
    dialog.close();

    if (orderStatus == undefined) {
        return;
    }
    const orderId = document.querySelector("#orderId").value;
    const response = await updatePayment(orderId, orderStatus);

    if (response.status != 200) {
        const errors = await response.json();
        const errorMessages = parseErrorMessages(errors.message);
        errorDialogMessage.innerHTML = errorMessages;
        errorDialog.showModal();
        return;
    }

    window.location.reload();
})


