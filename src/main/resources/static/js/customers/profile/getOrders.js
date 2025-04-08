import { get } from '../../http/api.js';


const customerId = document.getElementById("authenticated_customer_id").value;

export async function getOrders(...params) {
    let endpoint = `customers/${customerId}/orders`;
    if (params.length > 0) {
        endpoint += `?${params.join('&')}`;
    }

    const response = await get(endpoint);
    return response;
}