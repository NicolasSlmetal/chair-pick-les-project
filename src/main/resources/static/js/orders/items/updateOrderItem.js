import { patch } from '../../http/api.js';

export async function updateOrderItemStatus(orderId, orderItemId, status) {
    const response = await patch(`admin/orders/${orderId}/items/${orderItemId}/status`, { status });
    return response;

}