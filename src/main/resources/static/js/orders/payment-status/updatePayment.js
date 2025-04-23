import { patch } from '../../http/api.js';

export async function updatePayment(id, status) {
    const response = await patch(`admin/orders/${id}/status`, {status});
    return response;
}