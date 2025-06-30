package com.chairpick.ecommerce.services.task;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CartCheckTask.class, name = "CHECK_CART"),
        @JsonSubTypes.Type(value = CartExpirationNotificationTask.class, name = "CART_EXPIRATION_NOTIFICATION"),
        @JsonSubTypes.Type(value = CartExpirationAdviceTask.class, name = "CART_EXPIRATION_ADVICE"),
        @JsonSubTypes.Type(value = SendMailTask.class, name = "SEND_EMAIL"),
})
public abstract class Task<T> {
    T info;
    TaskType type;

    public Task() {

    }

}
