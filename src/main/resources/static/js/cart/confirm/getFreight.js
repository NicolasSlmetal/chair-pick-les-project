import { get } from '../../http/api.js';

export async function getFreight(chairId, addressId) {
    const response = await get("freight?chairId=" + chairId + "&addressId=" + addressId);
    return response;

}