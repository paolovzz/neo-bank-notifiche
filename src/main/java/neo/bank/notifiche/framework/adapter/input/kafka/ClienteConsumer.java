package neo.bank.notifiche.framework.adapter.input.kafka;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.kafka.api.IncomingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import neo.bank.notifiche.domain.enums.Messaggio;
import neo.bank.notifiche.framework.adapter.output.service.MailerOutputService;

@ApplicationScoped
@Slf4j
public class ClienteConsumer {

    @Inject
    private ObjectMapper mapper;

    @Inject
    private MailerOutputService mailerService;

    private static final String EVENT_OWNER = "CLIENTE";
    private static final String EVENT_CREAZIONE_CLIENTE_FALLITA = "CreazioneClienteFallita";


    @Incoming("cliente-notifications")
    @Blocking
    public CompletionStage<Void> consume(Message<String> msg) {
        return CompletableFuture.runAsync(() -> {
            var metadata = msg.getMetadata(IncomingKafkaRecordMetadata.class).orElseThrow();
            String eventType = new String(metadata.getHeaders().lastHeader("eventType").value());
            String aggregateName = new String(metadata.getHeaders().lastHeader("aggregateName").value());
            String payload = msg.getPayload();
            if (aggregateName.equals(EVENT_OWNER)) {
                JsonNode json = convertToJsonNode(payload);
                switch (eventType) {
                    case EVENT_CREAZIONE_CLIENTE_FALLITA:{
                        String emailCliente = json.get("emailCliente").asText();
                        mailerService.inviaEmail(emailCliente, Messaggio.CREAZIONE_CLIENTE_FALLITA.getOggetto(), Messaggio.CREAZIONE_CLIENTE_FALLITA.getCorpo());
                        break;
                    }
                    default:
                        log.warn("Evento [{}] non gestito...", eventType);
                        break;
                }
            } else {
                log.warn("Owner non gestito [{}]", aggregateName);
            }
        }).thenCompose(ignored -> msg.ack());
    }

    private JsonNode convertToJsonNode(String payload) {
        try {
            return mapper.readTree(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Errore durante la conversione json del messaggio kafka", e);
        }
    }
}
