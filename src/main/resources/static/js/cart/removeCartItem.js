import { httpDelete } from "../http/api.js";

const customerId = document.getElementById("authenticated_customer_id").value;

export async function removeCartItem(chairId) {
    const response = await httpDelete(`customers/${customerId}/cart/chair/${chairId}`);
    return response;
}