import { httpDelete } from '../../http/api.js';

const customerId = document.querySelector("#authenticated_customer_id").value;

export async function deleteCustomer() {
    const response = await httpDelete(`customers/${customerId}`);
    return response;
}