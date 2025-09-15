package org.model.accounts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.utils.RandomWordGenerator;

import java.util.Map;
import java.util.Objects;

@Getter
@Setter
public class OneTimeCode {
    Map<String, Object> cosaDelLogueo;
    String code;
    public OneTimeCode(){
        this.code = RandomWordGenerator.randomWord() +  RandomWordGenerator.randomWord() + RandomWordGenerator.randomWord() + RandomWordGenerator.randomWord() + RandomWordGenerator.randomWord() + RandomWordGenerator.randomWord();
        //20^6 combinaciones
    }
    public OneTimeCode(Map<String, Object> cosaDelLogueo){
        this.code = RandomWordGenerator.randomWord() +  RandomWordGenerator.randomWord() + RandomWordGenerator.randomWord() + RandomWordGenerator.randomWord() + RandomWordGenerator.randomWord() + RandomWordGenerator.randomWord();
        //20^6 combinaciones
        this.cosaDelLogueo = cosaDelLogueo;
    }

}
