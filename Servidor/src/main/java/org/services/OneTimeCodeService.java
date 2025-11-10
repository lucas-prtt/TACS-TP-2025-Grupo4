package org.services;

import org.exceptions.AccountNotFoundException;
import org.exceptions.WrongOneTimeCodeException;
import org.model.accounts.OneTimeCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class OneTimeCodeService {
    List<OneTimeCode> codigos = Collections.synchronizedList(new ArrayList<>()); // Thread safe


    @Scheduled(fixedRate = 900_000, initialDelay = 900_000) // Cada 15 minutos borra los codigos expirados
    public void removeExpiredCodes() {
        codigos.removeIf(OneTimeCode::isExpired);
    }

    /**
     * Busca un código de un solo uso por nombre de usuario.
     * @param username Nombre de usuario
     * @return El código encontrado
     * @throws AccountNotFoundException si no existe el usuario
     */
    public List<OneTimeCode> findByUsername(String username){
        List<OneTimeCode> codigosDelUsuario = codigos.stream()
                .filter(oneTimeCode -> Objects.equals(oneTimeCode.getCosaDelLogueo().get("username"), username))
                .filter(OneTimeCode::isValid).toList();
        if(codigosDelUsuario.isEmpty()){
            throw new WrongOneTimeCodeException();
        }
        return codigosDelUsuario;
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
