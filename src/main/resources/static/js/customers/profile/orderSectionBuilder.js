const statusMap = {
  "PENDING": "Em processamento",
  "APPROVED": "Aprovado",
"DELIVERING": "Em entrega",
"DELIVERED": "Entregue",
}

const classStatusMap = {
    "PENDING" : "warning",
    "APPROVED" : "success",
    "DELIVERING" : "warning",
    "DELIVERED" : "success",
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
            total.innerText = `Total: R$ ${(item.value * item.amount + item.freightValue).toLocaleString("pt-BR", {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
            textColumn.appendChild(total);
            const status = document.createElement("h2");
            status.innerHTML = `Status: <span class='${classStatusMap[item.status]}'> ${statusMap[item.status]} </span>`;
            textColumn.appendChild(status);
            divRowBorder.appendChild(textColumn);
            cardRow.appendChild(divRowBorder);
        }
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
        columnDiv.appendChild(cardRow);
    }
    section.appendChild(columnDiv);
    main.appendChild(section);
}

export function constructPaginatedOrderSection(orders, title) {


}