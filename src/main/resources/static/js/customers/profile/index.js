import { configureSearch } from "../../utils/configureSearch.js";
import { countCart } from "../../utils/countCart.js";
import { getOrders } from "./getOrders.js";
import { constructOrderSection } from "./orderSectionBuilder.js";
import { constructPaginatedOrderSection } from "./orderSectionBuilder.js";
import { postSwap } from "./postSwap.js";

const statusMap = {
    "PENDING": "Pedidos em processamento",
    "APPROVED": "Pedidos aprovados",
    "REPROVED": "Pedidos reprovados",
    "DELIVERING": "Pedidos em entrega",
    "DELIVERED": "Pedidos entregues",
}

const dialog = document.querySelector("#confirm__dialog");
const confirmButton = dialog.querySelector("#confirm__button");
const cancelButton = dialog.querySelector("#cancel__button");
const cancelOrderButton = document.querySelector("#cancel_order");
const deleteAccountButton = document.querySelector("#delete_account");
const swapDialog = document.querySelector("#swap__confirmation");
const swapAmountInput = swapDialog.querySelector("#swap__amount");
const swapCancelButton = swapDialog.querySelector("#cancel__button__swap");
const swapConfirmButton = swapDialog.querySelector("#confirm__button__swap");

swapCancelButton.addEventListener("click", () => {
    swapDialog.close();
});



const $ = window.jQuery;

//cancelOrderButton.addEventListener("click", () => {
//    configureModalAction(() => {
//        $(dialog).modal(":hide")
//        alert("Pedido cancelado com sucesso!");
//    }, "Cancelar pedido", "Tem certeza que deseja cancelar o pedido?");
//});

deleteAccountButton.addEventListener("click", () => {
    configureModalAction(() => {
        $(dialog).modal(":hide")
        alert("Conta deletada com sucesso!");
    }, "Deletar conta", "Tem certeza que deseja deletar sua conta?");
})  ;


configureSearch()

async function getOrderAndBuildSection(status) {
   const response = await getOrders(`status=${status}`);
   if (response.status !== 200) {
        return;
    }
    const orders = await response.json();
    if (orders.length === 0) {
        return;
    }
    constructOrderSection(orders, statusMap[status]);

}

function configureModalAction(action, title, message) {
    const dialogTitle = dialog.querySelector("h2");
    const dialogMessage = dialog.querySelector("p");

    dialogTitle.innerText = title;
    dialogMessage.innerText = message;

    dialog.showModal();

    confirmButton.onclick = action;
    cancelButton.onclick = () => dialog.close();
}


const itemsIds = document.querySelectorAll("input[name='itemId']")
itemsIds.forEach(itemId => {
    const parent = itemId.closest(".card");
    const requestSwapButton = parent.querySelector("button.danger");
    requestSwapButton.addEventListener("click", () => {
        configureModalAction(() => {
            dialog.close();
            alert("Pedido de troca realizado com sucesso!");
        }
        , "Solicitar troca"
        , "Tem certeza que deseja solicitar a troca do produto?")
    })
})

var swapItemId = undefined;
var swapOrderId = undefined;
swapConfirmButton.addEventListener("click", async () => {
    swapDialog.close();
    const amount = Number(swapAmountInput.value);
    if (isNaN(amount) || amount <= 0) {
        return;
    }

    if (!swapItemId || !swapOrderId) {
        return;
    }

    const response = await postSwap(swapOrderId, swapItemId, amount);

    if (response.status !== 201) {
        const errorJson = await response.json();
        const errorMessage = errorJson.message;
        return;
    }

    window.location.reload();

});

window.onload = async () => {
    await getOrderAndBuildSection("PENDING");
    await getOrderAndBuildSection("REPROVED");
    await getOrderAndBuildSection("APPROVED");
    const requestSwapButtons = document.querySelectorAll(".request_swap");
    const itemMaxAmountIdMap = {};
    const ordersId = [];
    requestSwapButtons.forEach(button => {
        const maxAmount = button.parentElement.querySelector("input[name='maxAmount']").value;
        const itemId = button.parentElement.querySelector("input[name='itemId']").value;
        itemMaxAmountIdMap[itemId] = maxAmount;
        const orderId = button.parentElement.querySelector("input[name='orderId']").value;
        ordersId.push(orderId);
        button.addEventListener("click", () => {
            const selectedItemId = button.parentElement.querySelector("input[name='itemId']").value;
            const selectedOrderId = button.parentElement.querySelector("input[name='orderId']").value;
            const maxAmount = itemMaxAmountIdMap[selectedItemId];
            swapItemId = Number(selectedItemId);
            swapOrderId = Number(selectedOrderId);

            swapAmountInput.setAttribute("max", maxAmount);
            swapDialog.showModal();
        });
    });
    await countCart();
}