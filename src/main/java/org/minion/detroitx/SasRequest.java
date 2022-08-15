package org.minion.detroitx;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.ToString;

/**
 * Request para SAS
 * @author jjalejandro
 * Dto para pedir el token de seguridad.
 * Se necesitan rellenar ciertos campos para poder
 * realizar la petición de token.
 * El modelo a seguir sale de la petición de muestra del 
 * Postman pero es posible que al final haya que poner otros datos
 */
@ToString
@Data
public class SasRequest implements Serializable{
    /**
     *  Serial version generated
     */
    private static final long serialVersionUID = -2382669131029333182L;
    private IdAttributes idAttributes;
    private String password;
    private String realm;
    private List<String> credentialType;
    
    /**
     * Clase interna que representa la estructura de
     * JSON
     * @author jjalejandro
     *
     */
    @Data
    public static class IdAttributes{
    	/**
    	 * Alias uid de nombre de usuario
    	 */
        private String uid;
    }    
}
