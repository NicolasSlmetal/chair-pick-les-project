import { post } from '../../http/api.js';

const customerId = document.getElementById("authenticated_customer_id").value;

export async function postSwap(orderId, itemId, amount) {
    const response = await post(`customers/${customerId}/orders/${orderId}/swaps`, {
        orderItemId: itemId,
        amount: amount
    });
    return response;

}