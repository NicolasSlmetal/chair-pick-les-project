import { put } from '../http/api.js';

const customerId = document.getElementById("authenticated_customer_id").value;

export async function putCartItem(chairId, quantity) {
    const response = await put(`customers/${customerId}/cart`, { chairId, amount: quantity });

    return response;
}