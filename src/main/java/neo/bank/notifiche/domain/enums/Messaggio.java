package neo.bank.notifiche.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Messaggio {
    
    CREAZIONE_CLIENTE_FALLITA ("Produra creazione utente fallita","La presente email per comunicarle che si e' verificato un errore durante la creazione del suo account.\nRiprovi o contatti la sua banca.");

    private String oggetto;
    private String corpo;
}

