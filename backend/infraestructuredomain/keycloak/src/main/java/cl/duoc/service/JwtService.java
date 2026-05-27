package cl.duoc.service;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.UrlJwkProvider;
import java.net.URI;
import java.net.URL;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        try {
            return urlJwkProvider.get(certsId.trim());
        } catch (Exception e) {
            // fallback: fetch all keys and return the first signing key
            ObjectMapper mapper = new ObjectMapper();
            JsonNode keys = mapper.readTree(url).get("keys");
            if (keys != null && keys.isArray()) {
                for (JsonNode key : keys) {
                    JsonNode use = key.get("use");
                    if (use != null && "sig".equals(use.asText())) {
                        String kid = key.get("kid").asText();
                        return urlJwkProvider.get(kid);
                    }
                }
                if (keys.size() > 0) {
                    String kid = keys.get(0).get("kid").asText();
                    return urlJwkProvider.get(kid);
                }
            }
            throw e;
        }
   }

}
