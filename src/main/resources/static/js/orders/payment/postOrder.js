import {post} from '../../http/api.js';
import {parseErrorMessages} from '../../utils/errorMessage.js';

const customerId = document.getElementById("authenticated_customer_id").value;
const errorDialog = document.querySelector("#error__modal");
const okButton = errorDialog.querySelector("button");
okButton.addEventListener("click", () => {
    errorDialog.close();
});

export async function postOrder(order) {
    const response = await post(`customers/${order.customerId}/orders`, order);
    if (response.status !== 201) {
        const pError = errorDialog.querySelector("p#errors");
        const error = await response.json();
        const errorMessages = parseErrorMessages(error.message);
        pError.innerHTML = errorMessages;
        errorDialog.showModal();

        return;
    }

    window.location.href = `/customers/${customerId}`;
}