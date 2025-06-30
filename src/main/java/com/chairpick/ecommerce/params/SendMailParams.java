package com.chairpick.ecommerce.params;

import com.chairpick.ecommerce.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SendMailParams {

    private User user;
    private String subject;
    private String content;
}
