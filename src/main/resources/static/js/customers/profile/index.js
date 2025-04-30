import { configureSearch } from "../../utils/configureSearch.js";
import { countCart } from "../../utils/countCart.js";
import { getOrders } from "./getOrders.js";
import { constructOrderSection, appendMoreOrdersToSection } from "./orderSectionBuilder.js";
import { constructPaginatedOrderSection } from "./orderSectionBuilder.js";
import { postSwap } from "./postSwap.js";
import { deleteOrder } from "./deleteOrder.js";

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
const errorDialog = document.querySelector("#error__modal");
const okButton = errorDialog.querySelector("button");
okButton.addEventListener("click", () => {
    errorDialog.close();
});

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

let mapFoundResultsByStatus = {
    "PENDING": 0,
    "APPROVED": 0,
    "REPROVED": 0
}
async function getOrderAndBuildSection(status, page=1   ) {
   const response = await getOrders(`status=${status}&page=${page}`);
   if (response.status !== 200) {
        return;
    }
    const json = await response.json();

    const orders = json.entitiesInPage;
    if (orders.length === 0) {
       return;
    }
    console.log({orders});
    const totalPages = json.totalResults;
    mapFoundResultsByStatus[status] += orders.length;

    if (page == 1) {
        constructOrderSection(orders, statusMap[status]);

    } else if (page > 1) {
        const createdSection = document.querySelector(`section.${status}`);
        appendMoreOrdersToSection(createdSection, orders);
    }

    if (totalPages > mapFoundResultsByStatus[status]) {
        const createdSection = document.querySelector(`section.${status}`);
        const button = document.createElement("button");
        button.innerText = "Carregar mais";
        button.classList.add("action__button");
        button.addEventListener("click", async () => {
            button.remove();

            await getOrderAndBuildSection(status, page + 1);
        });
        createdSection.appendChild(button);
    }
    reloadButtons();
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
        const pError = errorDialog.querySelector("p#error__message");
        pError.innerHTML = errorMessage;
        errorDialog.showModal();
        return;
    }

    window.location.reload();

});

window.onload = async () => {
    await getOrderAndBuildSection("PENDING");
    await getOrderAndBuildSection("REPROVED");
    await getOrderAndBuildSection("APPROVED");

    await countCart();
}

function reloadButtons() {
    const requestSwapButtons = document.querySelectorAll(".request_swap");
        const cancelOrderButtons = document.querySelectorAll(".cancel_order");
        const itemMaxAmountIdMap = {};
        const ordersId = [];

        cancelOrderButtons.forEach(button => {
            button.addEventListener("click", () => {
                const orderId = button.parentElement.querySelector("input[name='orderId']").value;
                configureModalAction(async () => {
                    dialog.close();
                    const response = await deleteOrder(orderId);
                    if (response.status !== 204) {
                        const errorJson = await response.json();
                        const errorMessage = errorJson.message;
                        const pError = errorDialog.querySelector("p#error__message");
                        pError.innerHTML = errorMessage;
                        errorDialog.showModal();
                        return;
                    }
                    window.location.reload();
                }, "Cancelar pedido", `Tem certeza que deseja cancelar o pedido ${orderId}?`);
            });
        });
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
}