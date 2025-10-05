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

    /**
     * Busca un código de un solo uso por nombre de usuario.
     * @param username Nombre de usuario
     * @return El código encontrado
     * @throws UserNotFoundException si no existe el usuario
     */
    public OneTimeCode findByUsername(String username){
        return codigos.stream().filter(oneTimeCode -> Objects.equals(oneTimeCode.getCosaDelLogueo().get("username"), username)).findFirst().orElseThrow(() -> new UserNotFoundException("No se encontro un One time code con ese usuario"));
    }
    /**
     * Elimina un código de un solo uso.
     * @param oneTimeCode Código a eliminar
     */
    public void delete(OneTimeCode oneTimeCode){
        codigos.remove(oneTimeCode);
    }
    /**
     * Agrega un nuevo código de un solo uso con los datos de login asociados.
     * @param cosaDelLogueo Información de login
     * @return El código generado
     */
    public OneTimeCode addNewCode(Map<String, Object> cosaDelLogueo){
        OneTimeCode code = new OneTimeCode(cosaDelLogueo);
        codigos.add(code);
        return code;
    }
}
