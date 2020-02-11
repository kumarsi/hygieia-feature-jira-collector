package com.capitalone.dashboard.config;

import com.capitalone.dashboard.util.Supplier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;

@Configuration
public class BeanConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanConfig.class);

    @Bean
    @Primary
    public Supplier<RestOperations> restOperationsSupplier() {
        LOGGER.warn("***** Bean restOperationsSupplier() invoked");

        return () -> {
            try {
                LOGGER.warn("*********** Trying to build a rest template");
                TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
                SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                        .loadTrustMaterial(null, acceptingTrustStrategy)
                        .build();
                SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

                CloseableHttpClient httpClient = HttpClients.custom()
                        .setSSLSocketFactory(csf)
                        .build();

                HttpComponentsClientHttpRequestFactory requestFactory =
                        new HttpComponentsClientHttpRequestFactory();

                requestFactory.setHttpClient(httpClient);
                RestTemplate restTemplate = new RestTemplate(requestFactory);
                return restTemplate;
            } catch (Exception e) {
                LOGGER.error("Error building rest template", e);
                throw new RuntimeException(e);
            }
        };
    }
}
