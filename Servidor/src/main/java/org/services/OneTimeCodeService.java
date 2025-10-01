package org.services;

import org.exceptions.UserNotFoundException;
import org.model.accounts.OneTimeCode;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class OneTimeCodeService {
    List<OneTimeCode> codigos = new ArrayList<>();

    public OneTimeCode findByUsername(String username){
        return codigos.stream().filter(oneTimeCode -> Objects.equals(oneTimeCode.getCosaDelLogueo().get("username"), username)).findFirst().orElseThrow(() -> new UserNotFoundException("No se encontro un One time code con ese usuario"));
    }
    public void delete(OneTimeCode oneTimeCode){

    codigos.remove(oneTimeCode);
    }
    public OneTimeCode addNewCode(Map<String, Object> cosaDelLogueo){
        OneTimeCode code = new OneTimeCode(cosaDelLogueo);
        codigos.add(code);
        return code;
    }
}
