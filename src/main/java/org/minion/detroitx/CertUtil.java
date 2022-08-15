package org.minion.detroitx;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

/**
 * CertUtil 
 * Utilidad para gestionar certificados
 * @author jjalejandro
 *
 */
@Slf4j
public final class CertUtil {

	/**
	 * Tipo de certificado
	 */
    private static final String TYPE = "PKCS12";

    /**
     * Avoid to create instances of this class
     */
    private CertUtil() {
        // Nothing to do
    }
    
    /**
     * Devuelve el path de un fichero 
     * sin el nombre del fichero
     * @param fileName datos de entrada
     * @return Path del fichero.
     */
    private static String getFilePath(String fileName) {
    	final String[] buffer = fileName.split("/");
    	final StringBuilder stringBuilder = new StringBuilder();
    	int i = 0;
    	for (String dir: buffer) {
    		if (i==buffer.length -1) {
    			continue;
    		}
    		stringBuilder.append(dir).append('/');    		
    	}
    	return stringBuilder.toString();
    }
    
    /**
     * Devuelve el path del fichero
     * @param fileName Nombre del fichero completo
     * @return Nombre del fichero
     */
    private static String getFileName(String fileName) {
    	final String[] buffer = fileName.split("/");
		final StringBuilder stringBuilder = new StringBuilder();
    	int i = 0;
    	for (String dir: buffer) {
    		if (i<buffer.length -1) {
    			continue;
    		}
    		stringBuilder.append(dir);
    	}
    	return stringBuilder.toString();
    }
    
    /**
     * Creates a new Certificate X509
     * @param certificate of type X509
     * @return the certification details.
     */
    private static CertDetails getCertDetails(final Certificate certificate) {
    	return new CertDetails().setX509Certificate((X509Certificate) certificate);
    }
    
    /**
     * Obtiene detalles del certificado
     * @param jksPath Path al keystore
     * @param jksPassword password del keystore
     * @return Mapa de certificados que se almacenan en 
     * el fichero keystore
     */
    public static Map<String, CertDetails> getCertificateDetails(String jksPath, String jksPassword) {
        final Map<String, CertDetails> buffer = new HashMap<>();
        if (Objects.nonNull(jksPath)) {        	
            File f = new File(getFilePath(jksPath), FilenameUtils.getName(getFileName(jksPath)));
            if (!f.exists()) {
                log.error("No se encuentra el fichero de almacen de claves {}", jksPath);
            }
            try (InputStream inStream = Files.newInputStream(Paths.get(FilenameUtils.getName(jksPath)))){
                final KeyStore keyStore = KeyStore.getInstance(TYPE);
                keyStore.load(inStream, jksPassword.toCharArray());
                for (final String alias : Collections.list(keyStore.aliases())) {
                    final Certificate certificate = keyStore.getCertificate(alias);
                    if (certificate instanceof X509Certificate) {
                        buffer.put(alias, getCertDetails(certificate));
                    }
                }
                return buffer;
            } catch (Exception e) {
                log.error("Error leyendo almacen de claves",e);
            }
        }
        return buffer;
    }
    
}