import { countCart } from '../../utils/countCart.js';
import { postOrder } from './postOrder.js';

const $ = window.jQuery;

const inputs = document.querySelectorAll('input.payment_value');
const cards = document.querySelectorAll('#credit_cards .card');
const promoCoupons = document.querySelectorAll('#promo_coupons .card');
const swapCoupons = document.querySelectorAll('#swap_coupons .card');
const couponGenerateMessage = document.querySelector('#generate_swap_coupon_message');
const checkboxes = document.querySelectorAll('#credit_cards .card input[type="checkbox"]');
const form = document.querySelector('form');
const pAdvice = document.querySelector('.warning');

let totalValue = Number(calculateOriginalValue());
const originalTotalValue = totalValue;

let lastRadio = null;

const cardCentsMap = new Map();

form.addEventListener('submit', async (event) => {
    event.preventDefault();
    const formData = new FormData(form);
    const data = Object.fromEntries(formData);
    data.coupons = [];
    data.creditCards = [];
    formData.getAll('credit_cards[]').forEach(cardId => {
        const objectCard = {};
        const cardDiv = document.querySelector(`input[name="cardId"][value="${cardId}"]`).parentElement;

        const cents = cardCentsMap.get(cardDiv) || 0;
        const numberValue = cents / 100;
        objectCard["value"] = numberValue;
        objectCard["id"] = Number(cardId);
        data.creditCards.push(objectCard);
    });
    data["coupons"].push(formData.get('coupon'));
    data["coupons"].push(formData.getAll('swap[]'));

    data["coupons"] = data["coupons"].flat().map(coupon => Number(coupon));
    data["coupons"] = data["coupons"].filter(coupon => !isNaN(coupon) && coupon !== 0);

    await postOrder(data);
});

initializeMoneyInputs(inputs);
initializeCardEvents();
initializePromoCouponEvents();
initializeSwapCouponEvents();

function calculateOriginalValue() {
    return Array.from(inputs).slice(0, 1).reduce((acc, input) => {
    const value = input.value.replace('R$ ', '').replace('.', '').replace(',', '.');
        if (isNaN(value) || value <= 0) {
            return originalTotalValue;
        }
        return acc + value;
    }, 0);
}

function initializeCardEvents() {
    cards.forEach(card => {
        card.addEventListener('click', handleCardClick);
    });
    checkboxes.forEach(checkbox => {
        checkbox.addEventListener('input', handleCheckboxChange);
    });
}

function initializePromoCouponEvents() {
    promoCoupons.forEach(promoCoupon => {
        const radio = promoCoupon.querySelector('input[type="radio"]');
        promoCoupon.addEventListener('click', (event) => handlePromoCouponClick(event, promoCoupon, radio));
        radio.addEventListener('change', preventDisabledChanges);
        radio.addEventListener('input', (event) => handlePromoCouponSelection(event, promoCoupon, radio));
    });
}

function initializeSwapCouponEvents() {
    swapCoupons.forEach(swapCoupon => {
        const check = swapCoupon.querySelector('input[type="checkbox"]');
        swapCoupon.addEventListener('click', (event) => handleSwapCouponClick(event, swapCoupon, check));
        swapCoupon.querySelector('input[name="couponValue"]').addEventListener('input', () => {
            toggleCheckbox(check);
        });
        check.addEventListener('input', () => handleSwapCouponSelection(swapCoupon, check));
    });
}

function initializeMoneyInputs(inputs) {
    inputs.forEach(input => {
        $(input).maskMoney({
            prefix: 'R$ ',
            allowNegative: false,
            thousands: '.',
            decimal: ',',
            affixesStay: false
        });
        $(input).on('change', () => handlePaymentValueChange(input));
    });
}

function handleCardClick(event) {
    if (event.target.tagName === 'INPUT' && event.target.type !== 'checkbox') {
        return;
    }
    const check = this.querySelector('input[type="checkbox"]');
    toggleCheckbox(check);
}

function toggleCheckbox(checkbox) {
    checkbox.checked = !checkbox.checked;
    checkbox.dispatchEvent(new Event('input'));
}

function handleCheckboxChange() {
    pAdvice.innerText = '';
    const selectedPromoCoupons = getSelectedPromoCoupon();
    const selectedSwapCoupons = getSelectedSwapCoupons();
    const parent = this.closest('.card');
    const inputValue = parent.querySelector('input.payment_value');
    const selectedCards = getSelectedCreditCards();

    if (this.checked) {
        handleCardSelected(this, parent, inputValue, selectedCards, selectedPromoCoupons, selectedSwapCoupons);
    } else {
        handleCardUnselected(this, inputValue, selectedCards);
    }
}

function handleCardSelected(checkbox, cardElement, inputValue, selectedCards, promoCoupons, swapCoupons) {
    if (totalValue <= 0) {
        checkbox.checked = false;
        return;
    }

    distributeValuesPrecisely(totalValue, selectedCards);
    applyCardValuesFromMap();
    const value = inputValue.value.replace('R$', '').replace(/\./g, '').replace(',', '.').trim();
    const floatValue = parseFloat(value);
    if (isNaN(floatValue) || floatValue <= 0) {
        checkbox.checked = false;
        inputValue.value = '';
        cardCentsMap.delete(cardElement);
        cardElement.removeAttribute('data-custom');
        return;
    }

    inputValue.readonly = false;
}

function handleCardUnselected(checkbox, inputValue, selectedCards) {
    if (selectedCards.length === 0 && getCouponsValue() < originalTotalValue) {
        checkbox.checked = true;
        inputValue.value = formatCurrency(totalValue);
        return;
    }
    inputValue.value = '';
    if (selectedCards.length > 0) {
        distributeValuesPrecisely(totalValue, selectedCards);
        applyCardValuesFromMap();
        inputValue.readonly = true;
    }
}

function handlePromoCouponClick(event, promoCoupon, radio) {
    if (event.target.tagName === 'BUTTON') {
        return;
    }
    if (event.target.tagName === 'INPUT') {
        toggleRadio(radio);
        return;
    }
    toggleRadio(radio);
}

function toggleRadio(radio) {
    radio.checked = !radio.checked;
    radio.dispatchEvent(new Event('input'));
}

function preventDisabledChanges(event) {
    if (event.target.disabled) {
        event.preventDefault();
    }
}

function handlePromoCouponSelection(event, promoCoupon, radio) {
    if (lastRadio !== null && lastRadio !== radio) {
        lastRadio.checked = false;
    }
    if (radio.getAttribute('disabled')) {
        event.preventDefault();
        radio.checked = false;
        if (lastRadio) lastRadio.checked = true;
        return;
    }
    const value = parseFloat(promoCoupon.querySelector('input[name="couponValue"]').value);
    if (radio.checked) {
        lastRadio = radio;
        totalValue -= value;
        verifyIfExceedsTheTotal(promoCoupon);
        return;
    }
    totalValue += value;
    verifyIfExceedsTheTotal(promoCoupon);
    limitCreditCardsIfNecessary();
}

function handleSwapCouponClick(event, swapCoupon, check) {
    if (event.target.tagName === 'BUTTON') {
        return;
    }
    if (event.target.tagName === 'INPUT') {
        toggleCheckbox(check);
        return;
    }
    toggleCheckbox(check);
}

function handleSwapCouponSelection(swapCoupon, check) {

    if (check.getAttribute('disabled')) {
        check.checked = false;
        return;
    }
    const value = parseFloat(swapCoupon.querySelector('input[name="couponValue"]').value);
    if (check.checked) {
        totalValue -= value;

        verifyIfExceedsTheTotal(swapCoupon);
        return;
    }
    totalValue += value;
    verifyIfExceedsTheTotal(swapCoupon);
    limitCreditCardsIfNecessary();
}


function handlePaymentValueChange(input) {
    const cardElement = input.parentElement;
    const checkbox = cardElement.querySelector('input[type="checkbox"]');
    const selectedCards = getSelectedCreditCards();
    const coupons = getSelectedPromoCoupon();
    const swapCoupons = getSelectedSwapCoupons();
    if (selectedCards.indexOf(cardElement) === -1) {
        return;
    }

    if (selectedCards.length === 1) {
        input.value = formatCurrency(totalValue);
        cardCentsMap.set(cardElement, Math.round(totalValue * 100));
        return;
    }

    const rawValue = input.value.replace('R$ ', '').replace(/\./g, '').replace(',', '.');
    const customValue = parseFloat(rawValue);

    if (!customValue || customValue > totalValue || customValue < 0) {
        pAdvice.innerText = 'Valor inválido';
        distributeValuesPrecisely(totalValue, selectedCards);
        applyCardValuesFromMap();
        return;
    }

    if (selectedCards.length > 1 && customValue < 10 && coupons.length === 0 && swapCoupons.length === 0) {
        pAdvice.innerText = 'Valor mínimo de R$ 10,00 para cada cartão';
        distributeValuesPrecisely(totalValue, selectedCards);
        applyCardValuesFromMap();
        return;
    }

    const customCents = Math.round(customValue * 100);

    cardElement.setAttribute('data-custom', 'true');
    cardCentsMap.set(cardElement, customCents);


    let customSum = 0;
    const nonCustomCards = [];
    selectedCards.forEach(card => {
        if (card.getAttribute('data-custom') === 'true') {
            customSum += cardCentsMap.get(card);
        } else {
            nonCustomCards.push(card);
        }
    });


    const totalCents = Math.round(totalValue * 100);
    const remainingCents = totalCents - customSum;


    if (nonCustomCards.length > 0) {
        distributeValuesForCustom(nonCustomCards, remainingCents);
    }

    applyCardValuesFromMap();
}


function distributeValuesForCustom(cards, remainingCents) {
    const count = cards.length;
    const base = Math.floor(remainingCents / count);
    const remainder = remainingCents % count;
    cards.forEach((card, index) => {
        let cents = base;
        if (index < remainder) {
            cents += 1;
        }
        cardCentsMap.set(card, cents);
    });
}

function verifyIfExceedsTheTotal(coupon) {
    const couponsValue = getCouponsValue();
    const totalValue = calculateOriginalValue();

    const difference = -(totalValue - couponsValue);
    if (difference > 0) {
        handleExcessCouponValue(coupon, difference);
    } else {
        handleNormalCouponValue(coupon, difference);
    }
}

function handleExcessCouponValue(coupon, difference) {
    disableUnselectedCoupons(coupon);
    const selectedCards = getSelectedCreditCards();
    clearCardSelections(selectedCards);
    showCouponGenerationMessage(difference);
}

function handleNormalCouponValue(coupon, difference) {
    couponGenerateMessage.textContent = '';
    enableAllCoupons(coupon);
    updateCreditCardSelections(-difference);
    if (difference == 0) {
        disableUnselectedCoupons();
    }
}

function disableUnselectedCoupons(coupon) {
    swapCoupons.forEach(otherCoupon => {
        const otherCheck = otherCoupon.querySelector('input[type="checkbox"]');
        if (!otherCheck.checked && otherCoupon !== coupon) {
            otherCheck.setAttribute('disabled', 'disabled');
        }
    });
    promoCoupons.forEach(promoCoupon => {
        const radio = promoCoupon.querySelector('input[type="radio"]');
        if (!radio.checked && promoCoupon !== coupon) {
            radio.setAttribute('disabled', 'disabled');
        }
    });
}

function enableAllCoupons(coupon) {
    swapCoupons.forEach(otherCoupon => {
        const otherCheck = otherCoupon.querySelector('input[type="checkbox"]');
        if (!otherCheck.checked && otherCoupon !== coupon) {
            otherCheck.removeAttribute('disabled');
        }
    });
    promoCoupons.forEach(promoCoupon => {
        const radio = promoCoupon.querySelector('input[type="radio"]');
        if (!radio.checked && promoCoupon !== coupon) {
            radio.removeAttribute('disabled');
        }
    });
}

function clearCardSelections(selectedCards) {
    selectedCards.forEach(card => {
        const check = card.querySelector('input[type="checkbox"]');
        const inputValue = card.querySelector('input.payment_value');
        check.checked = false;
        inputValue.value = '';
        cardCentsMap.delete(card);
        card.removeAttribute('data-custom');
    });
}

function showCouponGenerationMessage(difference) {
    couponGenerateMessage.textContent = `Será gerado um cupom de troca no valor de R$ ${difference.toFixed(2).replace('.', ',')}`;
}

function updateCreditCardSelections(remainingValue) {

    const selectedCards = getSelectedCreditCards();
    if (selectedCards.length === 0) {
        cardCentsMap.clear();
        selectDefaultCard(remainingValue);
        return;
    }
    totalValue = remainingValue;
    distributeValuesPrecisely(totalValue, selectedCards);
    applyCardValuesFromMap();
    selectedCards.forEach(card => {
        const checkbox = card.querySelector('input[type="checkbox"]');
        const input = card.querySelector('input.payment_value');
        const value = parseFloat(input.value.replace('R$', '').replace(/\./g, '').replace(',', '.').trim());
        const floatValue = parseFloat(value);
        if (isNaN(floatValue) || floatValue <= 0) {
            checkbox.checked = false;
            cardCentsMap.delete(card);
            card.removeAttribute('data-custom');
            input.value = '';
        }
    });
}

function selectDefaultCard(value) {
    if (value <= 0) {
        return;
    }
    const formattedValue = Math.round(value * 100)

    const defaultCard = document.querySelector('.card.default');
    cardCentsMap.set(defaultCard, formattedValue);
    defaultCard.querySelector('input.payment_value').value = formatCurrency(formattedValue / 100);
    defaultCard.querySelector('input[type="checkbox"]').checked = true;
}

function updateSelectedCardsValues(cards, value) {

    distributeValuesPrecisely(totalValue, cards);
    applyCardValuesFromMap();
}

function limitCreditCardsIfNecessary() {
    let selectedCards = getSelectedCreditCards();
    if (selectedCards.length === 0) {
        return;
    }
    if (totalValue <= 0) {
        totalValue = originalTotalValue - getCouponsValue();
    }
    let dividedValue = Math.trunc((totalValue / selectedCards.length) * 100) / 100;
    const promoCoupons = getSelectedPromoCoupon();
    const swapCoupons = getSelectedSwapCoupons();
    while (dividedValue < 10 &&
           promoCoupons.length === 0 &&
           swapCoupons.length === 0 &&
           selectedCards.length > 1) {
        const last = selectedCards[selectedCards.length - 1];
        last.querySelector('input[type="checkbox"]').checked = false;
        last.querySelector('input.payment_value').value = '';
        selectedCards.splice(selectedCards.indexOf(last), 1);
        dividedValue = totalValue / selectedCards.length;
    }
    console.log({ dividedValue, totalValue, selectedCards });
    distributeValuesPrecisely(totalValue, selectedCards);
    applyCardValuesFromMap();
}

function getSelectedCreditCards() {
    const allCards = document.querySelectorAll("#credit_cards .card");
    return Array.from(allCards).filter(card => card.querySelector("input[type='checkbox']").checked === true);
}

function getSelectedPromoCoupon() {
    const allPromo = document.querySelectorAll("#promo_coupons .card");
    return Array.from(allPromo).filter(card => card.querySelector("input[type='radio']:checked"));
}

function getSelectedSwapCoupons() {
    const allSwap = document.querySelectorAll("#swap_coupons .card");
    return Array.from(allSwap).filter(card => card.querySelector("input[type='checkbox']:checked"));
}

function getCouponsValue() {
    const promoCoupons = getSelectedPromoCoupon();
    const swapCoupons = getSelectedSwapCoupons();
    const promoSum = promoCoupons.reduce((acc, card) => {
        return acc + parseFloat(card.querySelector('input[name="couponValue"]').value);
    }, 0);
    const swapSum = swapCoupons.reduce((acc, card) => {
        return acc + parseFloat(card.querySelector('input[name="couponValue"]').value);
    }, 0);
    return promoSum + swapSum;
}

function formatCurrency(value) {
    return value.toLocaleString('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    });
}

function distributeValuesPrecisely(totalValue, selectedCards) {
    const totalCents = Math.round(totalValue * 100);

    const count = selectedCards.length;

    const base = Math.floor(totalCents / count);
    const remainder = totalCents % count;

    cardCentsMap.clear();

    selectedCards.forEach(card => {
        card.removeAttribute('data-custom');
    });
    selectedCards.forEach((card, index) => {
        let cents = base;
        if (index < remainder) {
            cents += 1;
        }

        cardCentsMap.set(card, cents);

    });
}


function applyCardValuesFromMap() {
    for (const [card, cents] of cardCentsMap.entries()) {

        const input = card.querySelector('input.payment_value');
        const value = cents / 100;

        input.value = formatCurrency(value);
    }
}

distributeValuesPrecisely(totalValue, getSelectedCreditCards());
countCart();