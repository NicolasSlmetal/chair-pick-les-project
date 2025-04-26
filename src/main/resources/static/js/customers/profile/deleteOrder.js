import { httpDelete } from '../../http/api.js';

const customerId = document.getElementById("authenticated_customer_id").value;

export async function deleteOrder(orderId) {
    const response = await httpDelete(`customers/${customerId}/orders/${orderId}`);
    return response;
}