import { post } from "../../http/api.js";

export function confirmPayment(data) {
    setCookieOfAddresses(data);

    window.location.href = `/customers/${data.customer_id}/orders/payment`;
}

function setCookieOfAddresses(data) {
    const customerId = data.customer_id;
    const deliveryAddressId = data.deliveryAddressId;
    const billingAddressId = data.billingAddressId;

    document.cookie = `billingAddressId=${billingAddressId}; path=/customers/${customerId}/orders/payment; samesite=strict;`;
    document.cookie = `deliveryAddressId=${deliveryAddressId}; path=/customers/${customerId}/orders/payment; samesite=strict;`;
}