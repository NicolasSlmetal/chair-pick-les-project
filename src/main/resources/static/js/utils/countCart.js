import { get } from "../http/api.js"

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
    }
}