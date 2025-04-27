import { patch } from '../http/api.js';

export async function patchSwapReceived(orderId, swapId, returnToStock) {
    const response = await patch(`admin/orders/${orderId}/swaps/${swapId}/confirm`, returnToStock);
    return response;
}