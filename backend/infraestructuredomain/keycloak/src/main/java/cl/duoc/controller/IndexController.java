package cl.duoc.controller;

import org.springframework.web.bind.annotation.RestController;
import com.auth0.jwk.Jwk;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import cl.duoc.exception.BusinessRuleException;
import cl.duoc.service.KeycloakRestService;
import cl.duoc.service.JwtService;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class IndexController {
    private Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private KeycloakRestService restService;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/roles")
    public ResponseEntity<?> getRoles(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            DecodedJWT jwt = JWT.decode(token);
            Jwk jwk = jwtService.getJwk();
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
            algorithm.verify(jwt);
            var realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null || realmAccess.isNull()) {
                throw new Exception("Token does not contain realm_access claim");
            }
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) realmAccess.asMap().get("roles");
            if (roles == null || roles.isEmpty()) {
                throw new Exception("Token does not contain roles in realm_access");
            }
            Date expiryDate = jwt.getExpiresAt();
            if (expiryDate.before(new Date())) {
                throw new Exception("Token ha expirado");
            }
            HashMap<String, Integer> hashMap = new HashMap<>();
            for (String role : roles) {
                hashMap.put(role, role.length());
            }
            return ResponseEntity.ok(hashMap);
        } catch (Exception e) {
            logger.error("Error retrieving roles: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/valid")
    public ResponseEntity<?> valid(@RequestHeader("Authorization") String authHeader) {
        try {
            restService.checkValidity(authHeader);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("is_valid", "true");
            }});
        } catch (Exception e) {
            logger.error("token is not valid, exception : {} ", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody java.util.Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        String login = restService.login(username, password);
       return ResponseEntity.ok(login);
    }

    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> logout(@RequestParam(value = "refresh_token", name = "refresh_token") String refreshToken) throws BusinessRuleException {
        try {
            restService.logout(refreshToken);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("logout", "true");
            }});
        } catch (Exception e) {
            logger.error("unable to logout, exception : {} ", e.getMessage());
            throw new BusinessRuleException("logout", "False",HttpStatus.FORBIDDEN);   
        }
    }  
    @PostMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> refresh(@RequestParam(value = "refresh_token", name = "refresh_token") String refreshToken) throws BusinessRuleException {
        try {            
            return ResponseEntity.ok(restService.refresh(refreshToken));
        } catch (Exception e) {
            logger.error("unable to refresh, exception : {} ", e.getMessage());
            throw new BusinessRuleException("refresh", "False",HttpStatus.FORBIDDEN);   
        }
    }  

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody java.util.Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String email = body.get("email");
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username and password are required"));
        }
        try {
            restService.registerUser(username, password, email);
            return ResponseEntity.ok(Map.of("message", "User registered successfully"));
        } catch (Exception e) {
            logger.error("unable to register, exception : {} ", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }
}
