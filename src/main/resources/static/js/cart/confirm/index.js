import { debounce } from "../debounceCartUpdate.js";
import { getFreight } from "./getFreight.js";
import { confirmPayment } from "./confirmPayment.js";

const addressNameElement = {
    "delivery":document.getElementById('delivery_addresses'),

    "billing":document.getElementById('billing_addresses')
}



let activeDeliveryRadio = addressNameElement["delivery"].querySelector('input[type="radio"]:checked');

const addAddressButton = document.getElementById('address');
const cards = document.querySelectorAll('.card');
const customerId = document.querySelector("#customer").value;
const deliveryRadios = addressNameElement["delivery"].querySelectorAll('input[type="radio"]');
const subtotal = document.querySelector('#total').value;

deliveryRadios.forEach(radio => {

    radio.addEventListener('change', () => {
        if (radio == activeDeliveryRadio) {
            return;
        }

        if (radio.checked == true) {
            activeDeliveryRadio = radio;
            const addressId = radio.value;
            const chairsIds = document.querySelectorAll("input[name='chair_id");
            let freight = 0.0;
            let error = false;
            const calculateFreight = debounce(async () => {

                for (const chairId of chairsIds) {
                    const chairCard = chairId.parentElement;
                    const freightResponse = await getFreight(chairId.value, addressId);

                    if (freightResponse.status !== 200) {
                        error = true;
                        break;
                    }
                    const freightValue = await freightResponse.json();
                    const freightText = chairCard.querySelector('.freight_value');
                    freightText.innerText = `Frete: R$ ${freightValue.value.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`;

                    freight += freightValue.value;

                }

                if (error) {
                    return;
                }

                const freightText = document.querySelector('#freight_value');
                freightText.innerText = `Frete: R$ ${freight.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`;
                const totalValueText = document.querySelector('#total_value');
                const totalValue = Number(subtotal) + freight;
                const freightInput = document.querySelector('#freight');
                freightInput.value = freight;

                totalValueText.innerText = `Total: R$ ${totalValue.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`;
                totalValueText.scrollIntoView({ smooth: true });
            }, 100);

            calculateFreight();

        }

    });

});

cards.forEach(card => {
    card.addEventListener('click', () => {
        const radio = card.querySelector('input[type="radio"]');
        radio.checked = true;
        radio.dispatchEvent(new Event('change'));
    });
})

addAddressButton.addEventListener('click', () => {

    window.location.href = `/customers/${customerId}/addresses/new`;
});

const form = document.querySelector('form');
form.addEventListener('submit', async (event) => {
    event.preventDefault();
    const formData = new FormData(form);
    const data = Object.fromEntries(formData);
    data.deliveryAddressId = getAddressActiveForType('delivery');
    data.billingAddressId = getAddressActiveForType('billing');
    confirmPayment(data);
});


function calcFreight() {
    const deliveryAddressChosen = document.querySelector('input[name="delivery"]:checked');
    if (!deliveryAddressChosen) {
        return;
    }
    // Request to calculate the freight, for now we will use a fixed value
    return 30.58;
}

function getAddressActiveForType(type) {
    const addressId = addressNameElement[type].querySelector('input[type="radio"]:checked');
    return addressId.value;
}