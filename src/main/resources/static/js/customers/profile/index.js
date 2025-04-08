import { configureSearch } from "../../utils/configureSearch.js";
import { getOrders } from "./getOrders.js";
import { constructOrderSection } from "./orderSectionBuilder.js";
import { constructPaginatedOrderSection } from "./orderSectionBuilder.js";

const statusMap = {
    "PENDING": "Pedidos em processamento",
    "APPROVED": "Pedidos aprovados",
    "DELIVERING": "Pedidos em entrega",
    "DELIVERED": "Pedidos entregues",
}

const dialog = document.querySelector("dialog");
const confirmButton = dialog.querySelector("#confirm__button");
const cancelButton = dialog.querySelector("#cancel__button");
const cancelOrderButton = document.querySelector("#cancel_order");
const deleteAccountButton = document.querySelector("#delete_account");
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
    console.log({orders});
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

window.onload = async () => {
    await getOrderAndBuildSection("PENDING");
}