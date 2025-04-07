import {post} from '../../http/api.js';

export async function postOrder(order) {
    const response = await post(`customers/${order.customerId}/orders`, order);
    if (response.status !== 201) {
        return;
    }

    window.location.href = `/`
}