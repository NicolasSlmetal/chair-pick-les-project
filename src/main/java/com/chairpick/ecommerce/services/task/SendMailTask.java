package com.chairpick.ecommerce.services.task;

import com.chairpick.ecommerce.params.SendMailParams;

public class SendMailTask extends Task<SendMailParams> {

    public SendMailTask() {
        super();
    }

    public SendMailTask(SendMailParams info) {
        super(info, TaskType.SEND_EMAIL);
    }
}
