package org.minion.detroitx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.net.ssl.HttpsURLConnection;

import org.minion.detroitx.SasRequest.IdAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Bean to manage the security token.
 * 
 * Performs a call to the SAS Server
 * to get the token we need to work with
 * 
 * @author jjalejandro
 */
@Slf4j
@Component
public class TokenServiceImpl  {

	
	private String urlAddress;
	
	private String networkIp; 
	
	
	/**
	 * Constructor
	 * The IP should be a network IP
	 * @param urlAddress DUreccion url del servicio de generacion de tokens
	 * @param networkIp IP for the socket
	 * 
	 */
	
	public TokenServiceImpl(@Value("${app.urlAddress}") String urlAddress, @Value("${app.networkIp}") String networkIp) {
		this.urlAddress = urlAddress;
		this.networkIp = networkIp;				
	} 
	
	/**
	 * Serialize a request for get a valid token with a generic user.
	 * @return
	 * @throws JsonProcessingException
	 */
	private Optional<String> serializeRequest(String userName, String userPass) {	
		final SasRequest securityRequest = new SasRequest();
		final List<String> credentialTypes = new ArrayList<>();
		credentialTypes.add("JWT");
		credentialTypes.add("TOKEN_CORP");
		securityRequest.setCredentialType(credentialTypes);
		IdAttributes idAttributes = new IdAttributes();
		idAttributes.setUid(userName);
		securityRequest.setIdAttributes(idAttributes);
		securityRequest.setPassword(userPass);
		securityRequest.setRealm("SantanderBCE");
		try {
			final ObjectMapper objectMapper = new ObjectMapper();
			return Optional.of(objectMapper.writeValueAsString(securityRequest));	
		} catch(JsonProcessingException ex) {
			log.error("Json Processing error", ex.toString());
		}
		return Optional.empty();
	}	
	
	/**
	 * Prepare connection with SAS
	 * @param input Data to send
	 * @return new connection to base-url
	 */
	private Optional<URLConnection> prepareConnection(String input) {		
		try {
			URL url = new URL(urlAddress);	
			URLConnection urlConnection = url.openConnection();
			HttpsURLConnection httpsUrlConnection = (HttpsURLConnection) urlConnection;
		
			if(urlConnection instanceof HttpsURLConnection) {
		        ((HttpsURLConnection)urlConnection)
		             .setSSLSocketFactory(TrustedSSLSocketFactory.get(null,"changeit", ""));
		    }
			httpsUrlConnection.setConnectTimeout(5000);
			httpsUrlConnection.setRequestMethod("POST");
			httpsUrlConnection.setRequestProperty("Content-Type", "application/json");
	
		
			httpsUrlConnection.setRequestProperty("Content-Length", String.format("%d", input.length()));
			httpsUrlConnection.setRequestProperty("Host", networkIp);
			httpsUrlConnection.setDoOutput(true);
			return Optional.of(httpsUrlConnection);
		} catch (IOException e) {			
			log.error("Error {}", e);
		}
		return Optional.empty();		
	}
	
	/**
	 * parseResult
	 * 
	 * Function to get a SAS Response Object
	 * @param result the string object SAS Response
	 * @return the token that we are looking for
	 */
	private Optional<String> parseResult(String result) {
		try {
			final ObjectMapper objectMapper = new ObjectMapper();
			SasResponse sasResponse = (objectMapper.readValue(result, SasResponse.class));
			if (sasResponse!=null) {
				return Optional.of(sasResponse.getTokenCorp());
			}
		} catch(JsonProcessingException ex) {
			log.error("Json Processing error", ex.toString());
		}
		return Optional.empty();
	}
	
	/**
	 * 
	 * getToken
	 * 
	 * Obtener el token de seguridad
	 * Realiza una conexión vía Http con los datos de
	 * seguridad especificados y finalmente nos devuleve el token
	 * @param userName el nombre de usuario
	 * @param userPass el password para ese usuario	 
	 * @return Security token
	 */
	public Optional<String> getToken(String userName, String userPass) {		
		Optional<String> input = this.serializeRequest(userName, userPass);
		if (input.isEmpty()) {
			throw new RuntimeException("failed_request");
		}
		log.info(input.get());
		Optional<URLConnection> connection = this.prepareConnection(input.get());
		
		if (connection.isEmpty()) {
			throw new RuntimeException("failed_connection 2");
		}
		HttpsURLConnection urlConnection = (HttpsURLConnection) connection.get();
		try (OutputStream os = urlConnection.getOutputStream()) {
			byte[] datain = input.get().getBytes("utf-8");
			os.write(datain, 0, datain.length);
		} catch(IOException ioex) {
			log.error("Error IO en la petición de token {}", ioex);
		}	
		log.info("Envio completo");
	
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
			StringBuilder response = new StringBuilder();
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim()).append('\n');
			}
			log.info(response.toString());
			return parseResult(response.toString());
		} catch(IOException ioex) {
			log.error("Error IO en la petición de token {}", ioex);
		}				
		return Optional.empty();
	}

}
	
