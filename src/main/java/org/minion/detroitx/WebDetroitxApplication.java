package org.minion.detroitx;
	
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Optional;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;



@SpringBootApplication
@Slf4j
public class WebDetroitxApplication implements ApplicationRunner {

	@Autowired
	private TokenServiceImpl tokenServiceImpl;
	
	public static void main(String[] args) {
		SpringApplication.run(WebDetroitxApplication.class, args);
	}

	public RestTemplate restTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

		SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy)
				.build();

		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

		requestFactory.setHttpClient(httpClient);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate;
	}
	

	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("Llamada con resttempltate");
		final String url = "https://serviciosfyc.pru.bsch/v1/confirming/users/addUser";
		
		final Optional<String> token = tokenServiceImpl.getToken("n11111", "prueba");
		HttpHeaders headers = new HttpHeaders();	
		if (token.isEmpty())
			throw new RuntimeException("No se ha conseguido el token");
		headers.add("Authorization", String.format("Bearer %s",token.get()));
		headers.add("Host", "192.168.77.96");
		
		headers.add("Accept", "*/*");
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Connection", "keep-alive");
		headers.add("X-ClientId", "nuwecf");
		
		HttpEntity<String> request = new HttpEntity<String>("{\"facade\": \"facade\"}",headers); 
		RestTemplate rt = this.restTemplate();
		ResponseEntity<String> respuesta = rt.postForEntity(new URI(url), request, String.class);
		log.info("Status Code {}", respuesta.getStatusCode().name());
		log.info("Response info {}", respuesta.getBody());
	}

}
