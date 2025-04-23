import { patch } from '../http/api.js';

export async function patchSwapStatus(orderId, swapId, status) {
    const response = await patch(`admin/orders/${orderId}/swaps/${swapId}`, {
        status
    });
    return response;
}