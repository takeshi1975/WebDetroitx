package org.minion.detroitx;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import lombok.Getter;
import lombok.Setter;

/**
 * Dto para detalles de un certificado de seguridad
 * @author jjalejandro
 *
 */
public class CertDetails {
	
	@Getter
	@Setter
    private PrivateKey privateKey;
	
	
    private X509Certificate x509Certificate;
    
    /**
     * get a Certificate 
     * @return certificate X509
     */
    public final X509Certificate getX509Certificate() {
    	return x509Certificate;
    }
    
	/**
	 * Set a valid X509 certificate
	 * @param x509Certificate Input certificate
	 * @return the certDetails object
	 */
    public CertDetails setX509Certificate(X509Certificate x509Certificate) {
        this.x509Certificate = x509Certificate;
        return this;
    }
}