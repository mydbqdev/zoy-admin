package com.integration.zoy;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHeaders;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.ssl.SslContextBuilder;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.SslProvider;

@SpringBootApplication
@EnableAsync
@ComponentScan(basePackages = { "com.integration.zoy", "com.integration.zoy.controller" , "com.integration.zoy.config","com.integration.zoy.service"})
public class ZoyAdminServerApplication {

	private final int TIMEOUT = (int) TimeUnit.SECONDS.toMillis(30);
	
	public static void main(String[] args) {
		SpringApplication.run(ZoyAdminServerApplication.class, args);
	}
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setReadTimeout(TIMEOUT);
		factory.setConnectTimeout(TIMEOUT);
		factory.setConnectionRequestTimeout(TIMEOUT);
		return new RestTemplate(factory);
	}
	
	@Bean
	public RestTemplate httpsRestTemplate() 
	                throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
	    TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
	    SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
	    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
	    CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
	    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
	    requestFactory.setHttpClient(httpClient);

	    RestTemplate restTemplate = new RestTemplate(requestFactory);
	    return restTemplate;
	 }
	
	@Bean
	public WebClient webClient() {
		HttpClient httpClient = HttpClient.create()
				  .secure(spec -> spec.sslContext(SslContextBuilder.forClient())
				  .defaultConfiguration(SslProvider.DefaultConfigurationType.TCP)
				  .handshakeTimeout(Duration.ofSeconds(30))
				  .closeNotifyFlushTimeout(Duration.ofSeconds(10))
				  .closeNotifyReadTimeout(Duration.ofSeconds(10)))
				  .responseTimeout(Duration.ofMillis(60000));
		return WebClient.builder()
		  .clientConnector(new ReactorClientHttpConnector(httpClient))
		  .defaultCookie("cookieKey", "cookieValue")
		  .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) 
	      .build();
	}
}
