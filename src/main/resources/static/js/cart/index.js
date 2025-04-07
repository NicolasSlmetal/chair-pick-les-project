import { configureSearch } from "../utils/configureSearch.js";
import { debounce } from "./debounceCartUpdate.js";
import { putCartItem } from "./putCartItem.js";
import { removeCartItem } from "./removeCartItem.js";

const upButtons = document.querySelectorAll('.up');
const downButtons = document.querySelectorAll('.down');
const removeButtons = document.querySelectorAll('.action__button.danger.expanded');
const confirmProductsButton = document.querySelector('.product_action');
const customerId = document.getElementById("authenticated_customer_id").value;

upButtons.forEach(button => {
    button.addEventListener('click', async () => {
        const parent = button.parentElement;
        const updateStatusMessage = parent.parentElement.querySelector('.update_status');
        const maxInput = parent.querySelector('input[name="max_quantity"]');
        const input = parent.querySelector('input[name="quantity"]');
        const max = parseInt(maxInput.value);
        const quantity = parseInt(input.value);
        updateStatusMessage.innerText = "";

        if (quantity < max) {
            disableConfirmButtonForWhile();
            updateStatusMessage.innerText = "Atualizando...";

            const p = parent.querySelector('p');
            p.innerHTML = `<strong>Quantidade:</strong> ${quantity + 1}`;
            const chairId = parseInt(parent.querySelector('input[name="chair_id"]').value);

            const update = debounce(async () => {
                enableConfirmButton();
                const response = await putCartItem(chairId, quantity + 1);
                if (response.status === 200) {
                    updateStatusMessage.innerText = "Atualizado!";
                    input.value = quantity + 1;
                } else {
                    updateStatusMessage.innerText = "Erro ao atualizar!";
                    p.innerHTML = `<strong>Quantidade:</strong> ${quantity}`;
                }
            }, 1000);
            update();
        }
    })
});

downButtons.forEach(button => {
    button.addEventListener('click', async () => {
        const parent = button.parentElement;
        const input = parent.querySelector('input[name="quantity"]');
        const quantity = parseInt(input.value);
        const updateStatusMessage = parent.parentElement.querySelector('.update_status');
        updateStatusMessage.innerText = "";
        if (quantity > 1) {
            disableConfirmButtonForWhile();
            updateStatusMessage.innerText = "Atualizando...";
            input.value = quantity - 1;
            const p = parent.querySelector('p');
            p.innerHTML = `<strong>Quantidade</strong>: ${quantity - 1}`;
            const chairId = parent.querySelector('input[name="chair_id"]').value;
            const update = debounce(async () => {
                enableConfirmButton();
                const response = await putCartItem(chairId, quantity - 1);
                if (response && response.status === 200) {
                    updateStatusMessage.innerText = "Atualizado!";
                } else {
                     updateStatusMessage.innerText = "Erro ao atualizar!";
                     p.innerHTML = `<strong>Quantidade:</strong> ${quantity}`;
                }
            }, 1000);
            update();
        }
    })
});

removeButtons.forEach(button => {
    button.addEventListener('click', () => {
        const parent = button.parentElement;
        const updateStatusMessage = parent.querySelector('.update_status');
        updateStatusMessage.innerText = "Removendo...";
        const chairId = parent.querySelector('input[name="chair_id"]').value;
        const deleteAction = debounce(async () => {
            const response = await removeCartItem(chairId);
            if (response.status === 204) {
                updateStatusMessage.innerText = "Removido!";
                parent.remove();
                displayMessageIfCartIsEmpty();
            } else {
                updateStatusMessage.innerText = "Erro ao remover!";
            }
        }, 1000);
        deleteAction();
    })
});



confirmProductsButton.addEventListener('click', () => {
    window.location.href = `/customers/${customerId}/cart/confirm`;
});

function displayMessageIfCartIsEmpty() {
    const products = document.querySelector('.products .row');
    const message = document.querySelector('.message');

    if (products.children.length === 0) {
        message.style.display = 'block';
        disableConfirmButtonForWhile();
    } else {
        message.style.display = 'none';
        enableConfirmButton();
    }
}

function disableConfirmButtonForWhile() {
    const confirmProductsButton = document.querySelector('.product_action');
    confirmProductsButton.style.display = 'none';
}

function enableConfirmButton() {
    const confirmProductsButton = document.querySelector('.product_action');
    confirmProductsButton.style.display = 'block';
}

configureSearch();

displayMessageIfCartIsEmpty();