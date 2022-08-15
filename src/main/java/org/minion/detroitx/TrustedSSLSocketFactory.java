package org.minion.detroitx;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

import lombok.extern.slf4j.Slf4j;

/**
 * Used for ssl tests to simplify setup.
 */
@Slf4j
public final class TrustedSSLSocketFactory extends SSLSocketFactory implements X509TrustManager, X509KeyManager {


	
    private static final Map<String, SSLSocketFactory> sslSocketFactories = new LinkedHashMap<String, SSLSocketFactory>();    
    private static final String[] ENABLED_CIPHER_SUITES = { 
            "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
            "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
            "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
            "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256" 
      };
    private final SSLSocketFactory delegate;
    private final String serverAlias;
    private final PrivateKey privateKey; 
    private X509Certificate[] certificateChain;
    
    private TrustedSSLSocketFactory(String pathToJks, String sslPassword, String serverAlias) {
        certificateChain = null;
        privateKey = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(new KeyManager[] { this }, new TrustManager[] { this }, new SecureRandom());
            this.delegate = sc.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.serverAlias = serverAlias;
        try {
            Map<String, CertDetails> result = CertUtil.getCertificateDetails(pathToJks, sslPassword);
            if (!result.isEmpty()) {
                this.certificateChain = result.values().stream().map(p -> p.getX509Certificate())
                        .toArray(X509Certificate[]::new);
            } else {
                log.warn("No certified was found");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static SSLSocketFactory get(String pathToJks, String sslPassword) {
        return get(pathToJks, sslPassword, "");
    }

    public synchronized static SSLSocketFactory get(String pathToJks, String sslPassword, String serverAlias) {
        if (!sslSocketFactories.containsKey(serverAlias)) {
            sslSocketFactories.put(serverAlias, new TrustedSSLSocketFactory(pathToJks, sslPassword, serverAlias));
        }
        return sslSocketFactories.get(serverAlias);
    }

    static Socket setEnabledCipherSuites(Socket socket) {
        SSLSocket.class.cast(socket).setEnabledCipherSuites(ENABLED_CIPHER_SUITES);
        return socket;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return ENABLED_CIPHER_SUITES;
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return ENABLED_CIPHER_SUITES;
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return setEnabledCipherSuites(delegate.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return setEnabledCipherSuites(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return setEnabledCipherSuites(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
        return setEnabledCipherSuites(delegate.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
            throws IOException {
        return setEnabledCipherSuites(delegate.createSocket(address, port, localAddress, localPort));
    }
        
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    /**
     * Hay que validar el cliente
     */
    public void checkClientTrusted(X509Certificate[] certs, String authType) {
    	log.info("CheckClientTrusted");
    }
    
    /**
     * Hay que validar el servidor.
     */
    public void checkServerTrusted(X509Certificate[] certs, String authType) {
    	log.info("CheckClientTrusted");
    }

    @Override
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        return null;
    }

    @Override
    public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        return null;
    }

    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return null;
    }

    @Override
    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        return serverAlias;
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        return certificateChain;
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {
        return privateKey;
    }
}