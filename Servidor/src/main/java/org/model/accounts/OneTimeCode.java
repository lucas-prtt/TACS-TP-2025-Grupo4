package org.model.accounts;

import lombok.Getter;
import lombok.Setter;
import org.utils.RandomWordGenerator;

import java.util.Map;

@Getter
@Setter
public class OneTimeCode {
    Map<String, Object> cosaDelLogueo;
    String code;
    /**
     * Constructor por defecto. Genera un código aleatorio de un solo uso.
     */
    public OneTimeCode(){
        this.code = RandomWordGenerator.randomWord() +  RandomWordGenerator.randomWord() + RandomWordGenerator.randomWord() + RandomWordGenerator.randomWord() + RandomWordGenerator.randomWord() + RandomWordGenerator.randomWord();
        //20^6 combinaciones
    }
    /**
     * Constructor que genera un código aleatorio y asocia datos de login.
     * @param cosaDelLogueo Información de login asociada al código
     */
    public OneTimeCode(Map<String, Object> cosaDelLogueo){
        this.code = RandomWordGenerator.randomWord()  + "-" +  RandomWordGenerator.randomWord() + "-" + RandomWordGenerator.randomWord() + "-" + RandomWordGenerator.randomWord() + "-" + RandomWordGenerator.randomWord() + "-" + RandomWordGenerator.randomWord();
        //20^6 combinaciones = 64_000_000
        this.cosaDelLogueo = cosaDelLogueo;
    }

}
