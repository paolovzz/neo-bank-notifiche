package neo.bank.notifiche.framework.adapter.output.service;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class MailerOutputService{
    
    @Inject
    private Mailer mailer;
    
    public void inviaEmail(String destinatario, String oggetto,String testo ) {
        log.info("Invio email a [{}]", destinatario);
         mailer.send(
        Mail.withText(destinatario, "OGGETTO DI TEST!", testo)
    );
    }

}
