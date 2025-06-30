package com.chairpick.ecommerce.services.task.handler;

import com.chairpick.ecommerce.services.MailService;
import com.chairpick.ecommerce.services.task.SendMailTask;
import com.chairpick.ecommerce.services.task.Task;
import com.chairpick.ecommerce.services.task.interfaces.TaskConfirmation;
import org.springframework.stereotype.Service;

@Service
public class SendMailTaskHandler implements TaskHandler {

    private final MailService mailService;

    public SendMailTaskHandler(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    public <T> void handle(Task<T> task, TaskConfirmation confirmation) {

        SendMailTask sendMailTask = convertToSendMailTask(task);
        int tries = 3;

        while (tries > 0) {
            try {
                mailService.sendEmail(sendMailTask);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("An error occurred while sending email: " + e.getMessage());
                System.out.println("Remaining tries: " + (tries - 1));
                tries--;
            }
        }

        confirmation.confirm();
    }


    private static <T> SendMailTask convertToSendMailTask(Task<T> task) {
        if (task instanceof SendMailTask sendMailTask) {
            return sendMailTask;
        }
        throw new IllegalArgumentException("Invalid task type for SendMailTaskHandler");
    }
}
