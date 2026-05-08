package cl.duoc.service;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.UrlJwkProvider;
import java.net.URI;
import java.net.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    @Value("${keycloak.jwk.url}")
    private String jwkUrl;

    @Value("${keycloak.certs-id}")
    private String certsId;

   @Cacheable(value = "jwkCache")
   public Jwk getJwk() throws Exception {
    URL url = URI.create(jwkUrl).toURL();
    UrlJwkProvider urlJwkProvider = new UrlJwkProvider(url);
    Jwk get = urlJwkProvider.get(certsId.trim());  
    return get;
   }

}
