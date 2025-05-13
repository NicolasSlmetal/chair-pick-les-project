import { get, httpDelete } from "../http/api.js"

const customerId = document.getElementById("authenticated_customer_id");
export async function countCart() {
    
    if (!customerId) {
        return;
    }
    const customerIdValue = customerId.value;
    const response = await get(`customers/${customerIdValue}/cart/count`);
    if (response.status !== 200) {
        return;
    }
    const cart = await response.text();
    const counter = document.querySelector(".cart__counter");
    if (counter && cart) {
        counter.innerHTML = `<strong>${cart}</strong>`;
        await verifyIfHasExpiredItems();
    }
}

async function verifyIfHasExpiredItems() {
    if (!customerId) {
        return;
    }
    const customerIdValue = customerId.value;
    const response = await get(`customers/${customerIdValue}/cart/expired`);
    if (response.status !== 200) {
        return;
    }
    const expiredItems = await response.json();
    if (expiredItems.length > 0) {
        createDialogToConfirmExpiredItems(expiredItems);
    }
}

export function createDialogToConfirmExpiredItems(items) {
    const dialog = document.createElement("dialog");
    dialog.setAttribute("id", "expired-items-dialog");
    dialog.setAttribute("class", "large_dialog");
    const title = document.createElement("h2");
    title.innerText = "Itens expirados";
    dialog.appendChild(title);
    const message = document.createElement("p");
    dialog.appendChild(message);
    message.innerText = "Os seguintes itens do carrinho estão expirados:";
    const list = document.createElement("ul");
    list.setAttribute("class", "list");
    const chairSet = new Set(items.map(item => item.item.chair.name));
    chairSet.forEach(name => {
        const listItem = document.createElement("li");
        listItem.innerText = name;
        list.appendChild(listItem);
    });
    dialog.appendChild(list);
    const button = document.createElement("button");
    button.innerText = "Remover todos";
    button.setAttribute("class", "action__button");
    button.addEventListener("click", async () => {
        dialog.close();
        const customerIdValue = customerId.value;
        const response = await httpDelete(`customers/${customerIdValue}/cart/expired`);
        if (response.status !== 204) {
            return;
        }

        window.location.reload();
    });
    dialog.appendChild(button);
    document.body.appendChild(dialog);
    dialog.showModal();
}