const statusMap = {
  "PENDING": "Em processamento",
  "APPROVED": "Aprovado",
  "REPROVED": "Reprovado",
  "DELIVERING": "Em entrega",
  "DELIVERED": "Entregue",
  "SWAP_REQUEST": "Troca solicitada",
  "SWAP_REPROVED": "Troca reprovada",
  "IN_SWAP": "Em troca",
  "SWAPPED": "Trocado",
}

const classStatusMap = {
    "PENDING" : "warning",
    "APPROVED" : "success",
    "REPROVED" : "danger-text",
    "DELIVERING" : "warning",
    "DELIVERED" : "success",
    "SWAP_REQUEST" : "warning",
    "SWAP_REPROVED" : "danger-text",
    "IN_SWAP" : "warning",
    "SWAPPED" : "success",
}


export function constructOrderSection(orders, title) {
    const main = document.querySelector("main");
    const section = document.createElement("section");
    section.classList.add("profile");
    const titleText = document.createElement("h2");
    titleText.innerText = title;
    section.appendChild(titleText);
    const columnDiv = document.createElement("div");
    columnDiv.classList.add("column");

    for (const order of orders) {
        const cardRow = document.createElement("div");
        cardRow.classList.add("card");
        cardRow.classList.add("row");
        cardRow.classList.add("without_cursor");
        let orderTotalFreight = 0;
        const orderTotalValue = order.totalValue;
        for (const item of order.items) {
            const divRowBorder = document.createElement("div");
            divRowBorder.classList.add("row");
            divRowBorder.classList.add("border");
            const textColumn = document.createElement("div");
            textColumn.classList.add("column");
            const image = document.createElement("img");
            image.src = `/images/chairs/${item.item.chair.id}`;
            image.alt = item.item.chair.name;
            image.classList.add("product_image")
            divRowBorder.appendChild(image);
            const name = document.createElement("h2");
            name.innerText = item.item.chair.name;
            textColumn.appendChild(name);
            const amount = document.createElement("h2");
            amount.innerText = `Quantidade: ${item.amount}`;
            textColumn.appendChild(amount);
            const freight = document.createElement("h2");
            orderTotalFreight += item.freightValue;
            freight.innerText = `Frete: R$ ${item.freightValue.toLocaleString("pt-BR", {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
            textColumn.appendChild(freight);
            const subtotal = document.createElement("h2");
            subtotal.innerText = `Subtotal: R$ ${item.value.toLocaleString("pt-BR", {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
            textColumn.appendChild(subtotal);
            const total = document.createElement("h2");
            total.innerText = `Total: R$ ${(item.value + item.freightValue).toLocaleString("pt-BR", {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
            textColumn.appendChild(total);
            const status = document.createElement("h2");
            console.log(item.status);
            status.innerHTML = `Status: <span class='${classStatusMap[item.status]}'> ${statusMap[item.status]} </span>`;
            textColumn.appendChild(status);
            createButtonsForOrderItemStatus(textColumn, item, order);
            divRowBorder.appendChild(textColumn);
            cardRow.appendChild(divRowBorder);

        }
        const orderId = document.createElement("h2");
        orderId.innerText = `Pedido: ${order.id}`;
        cardRow.appendChild(orderId);
        const subtotalOrder = document.createElement("h2");
        subtotalOrder.innerText = `Subtotal: R$ ${(orderTotalValue - orderTotalFreight).toLocaleString("pt-BR", {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
        cardRow.appendChild(subtotalOrder);
        const freightOrder = document.createElement("h2");
        freightOrder.innerText = `Frete: R$ ${orderTotalFreight.toLocaleString("pt-BR", {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
        cardRow.appendChild(freightOrder);
        const totalOrder = document.createElement("h2");
        totalOrder.innerText = `Total: R$ ${(orderTotalValue).toLocaleString("pt-BR", {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
        const statusOrder = document.createElement("h2");
        statusOrder.innerHTML = `Status: <span class='${classStatusMap[order.status]}'> ${statusMap[order.status]} </span> `;
        cardRow.appendChild(totalOrder);
        cardRow.appendChild(statusOrder);
        createButtonsForOrderStatus(cardRow, order);
        columnDiv.appendChild(cardRow);
    }
    section.appendChild(columnDiv);
    main.appendChild(section);
}

export function constructPaginatedOrderSection(orders, title) {


}

function createButtonsForOrderItemStatus(element, orderItem, order) {
    if (orderItem.status == "DELIVERED") {
        const requestSwapButton = document.createElement("button");
        requestSwapButton.classList.add("danger");
        requestSwapButton.classList.add("action__button");
        requestSwapButton.classList.add("request_swap");
        requestSwapButton.innerText = "Solicitar troca";
        const maxAmount = document.createElement("input");
        maxAmount.type = "hidden";
        maxAmount.name = "maxAmount";
        maxAmount.value = orderItem.amount;
        const itemId = document.createElement("input");
        itemId.type = "hidden";
        itemId.name = "itemId";
        itemId.value = orderItem.id;
        const orderId = document.createElement("input");
        orderId.type = "hidden";
        orderId.name = "orderId";
        orderId.value = order.id;
        element.appendChild(orderId);
        element.appendChild(maxAmount);
        element.appendChild(itemId);
        element.appendChild(requestSwapButton);
    }
}

function createButtonsForOrderStatus(element, order) {
    if (order.status == "REPROVED") {
        const alterPaymentButton = document.createElement("a");
        alterPaymentButton.classList.add("action__button");
        alterPaymentButton.classList.add("fill");
        alterPaymentButton.classList.add("warning");
        alterPaymentButton.innerText = "Alterar pagamento";
        alterPaymentButton.href = `/orders/${order.id}/editPayment`;
        const cancelOrderButton = document.createElement("button");
        cancelOrderButton.classList.add("cancel_order");
        cancelOrderButton.classList.add("action__button")
        cancelOrderButton.classList.add("danger");
        cancelOrderButton.innerText = "Cancelar pedido";
        element.appendChild(alterPaymentButton);
        element.appendChild(cancelOrderButton);

     }
}