package org.model.accounts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.utils.OneTimeCodeGenerator;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Getter
@Setter
public class OneTimeCode {
    Map<String, Object> cosaDelLogueo;
    String code;
    @JsonIgnore
    private final Instant creationTime = Instant.now();

    /**
     * Constructor por defecto. Genera un código aleatorio de un solo uso.
     */
    public OneTimeCode(){
        this.code = OneTimeCodeGenerator.generateCode();
    }
    /**
     * Constructor que genera un código aleatorio y asocia datos de login.
     * @param cosaDelLogueo Información de login asociada al código
     */
    public OneTimeCode(Map<String, Object> cosaDelLogueo){
        this.code = OneTimeCodeGenerator.generateCode();;
        this.cosaDelLogueo = cosaDelLogueo;
    }

    public Boolean isExpired() {
        return Instant.now().isAfter(creationTime.plus(Duration.ofMinutes(5)));
    }

    public Boolean isValid(){
        return !isExpired();
    }

}
