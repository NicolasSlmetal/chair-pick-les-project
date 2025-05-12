import { Client } from '@stomp/stompjs';
import { createDialogToConfirmExpiredItems } from './countCart.js';

const customerId = document.getElementById("authenticated_customer_id");

const client = new Client({
    brokerURL: 'ws://localhost:8080/ws',
    onConnect: () => {
        console.log('Connected to WebSocket server');
        client.subscribe('/user/notifications/cart-expiration', (message) => {

            createDialogToConfirmExpiredItems([JSON.parse(message.body)]);
        });

    }
});


if (customerId) {
    const customerIdValue = customerId.value;
    client.connectHeaders = {
        'customerId': customerIdValue
    };
    client.activate();
}
