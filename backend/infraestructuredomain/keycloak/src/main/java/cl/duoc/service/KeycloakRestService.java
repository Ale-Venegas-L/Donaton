package cl.duoc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KeycloakRestService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${keycloak.token-uri}")
    private String keycloakTokenUri;

    @Value("${keycloak.user-info-uri}")
    private String keycloakUserInfo;

    @Value("${keycloak.logout}")
    private String keycloakLogout;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.authorization-grant-type}")
    private String grantType;
    
    @Value("${keycloak.authorization-grant-type-refresh}")
    private String grantTypeRefresh;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.scope}")
    private String scope;

    @Value("${keycloak.admin.username}")
    private String adminUsername;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    public String login(String username, String password) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username",username);
        map.add("password",password);
        map.add("client_id",clientId);
        map.add("grant_type",grantType);
        map.add("client_secret",clientSecret);
        map.add("scope",scope);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, new HttpHeaders());
        return restTemplate.postForObject(keycloakTokenUri, request, String.class);
    }

    public String checkValidity(String token) throws Exception {
        return getUserInfo(token);
    }

    public void logout(String refreshToken) throws Exception {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id",clientId);
        map.add("client_secret",clientSecret);
        map.add("refresh_token",refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, new HttpHeaders());
        restTemplate.postForObject(keycloakLogout, request, String.class);
    }

    public String refresh(String refreshToken) throws Exception {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id",clientId);
        map.add("client_secret",clientSecret);
        map.add("refresh_token",refreshToken);
        map.add("grant_type",grantTypeRefresh);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, new HttpHeaders());
        return restTemplate.postForObject(keycloakTokenUri, request, String.class);
    }

    public List<String> getRoles(String token) throws Exception {
        String response = getUserInfo(token);
        @SuppressWarnings("unchecked")
        Map<String, Object> map = new ObjectMapper().readValue(response, HashMap.class);
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) map.get("roles");
        return roles;
    }

    public String getUserInfo(String token) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(keycloakUserInfo, org.springframework.http.HttpMethod.GET, entity, String.class).getBody();
    }

    public String getAdminToken() {
        String adminTokenUri = keycloakTokenUri.replace("/realms/Donaton/", "/realms/master/");
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", "admin-cli");
        map.add("username", adminUsername);
        map.add("password", adminPassword);
        map.add("grant_type", "password");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, new HttpHeaders());
        return restTemplate.postForObject(adminTokenUri, request, String.class);
    }

    public void registerUser(String username, String password, String email, String firstName, String lastName) {
        String tokenResponse = getAdminToken();
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> tokenMap = new ObjectMapper().readValue(tokenResponse, HashMap.class);
            String accessToken = (String) tokenMap.get("access_token");

<<<<<<< HEAD
            String adminUsersUri = keycloakTokenUri.replace("/realms/Donaton/protocol/openid-connect/token", "")
                    .replace("/auth", "") + "/admin/realms/Donaton/users";
=======
            String adminBaseUri = keycloakTokenUri.replace("/realms/Donaton/protocol/openid-connect/token", "")
                    .replace("/auth", "");
            String adminUsersUri = adminBaseUri + "/admin/realms/Donaton/users";
>>>>>>> develop

            Map<String, Object> user = new HashMap<>();
            user.put("username", username);
            user.put("email", email);
            user.put("firstName", firstName != null ? firstName : username);
            user.put("lastName", lastName != null ? lastName : username);
            user.put("enabled", true);
            user.put("emailVerified", true);
            user.put("requiredActions", List.of());

            Map<String, Object> credential = new HashMap<>();
            credential.put("type", "password");
            credential.put("value", password);
            credential.put("temporary", false);
            user.put("credentials", List.of(credential));

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(user, headers);

<<<<<<< HEAD
            restTemplate.exchange(adminUsersUri, HttpMethod.POST, request, String.class);
=======
            ResponseEntity<String> response = restTemplate.exchange(adminUsersUri, HttpMethod.POST, request, String.class);

            if (response.getStatusCode() == HttpStatus.CREATED) {
                assignUserRole(accessToken, adminBaseUri, username);
            }
>>>>>>> develop
        } catch (Exception e) {
            throw new RuntimeException("Failed to register user: " + e.getMessage(), e);
        }
    }
<<<<<<< HEAD
=======

    private void assignUserRole(String accessToken, String adminBaseUri, String username) {
        try {
            // 1. Get User ID
            String usersUri = adminBaseUri + "/admin/realms/Donaton/users?username=" + username;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            HttpEntity<String> userRequest = new HttpEntity<>(headers);
            String usersResponse = restTemplate.exchange(usersUri, HttpMethod.GET, userRequest, String.class).getBody();
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> users = new ObjectMapper().readValue(usersResponse, List.class);
            if (users == null || users.isEmpty()) return;
            String userId = (String) users.get(0).get("id");

            // 2. Get Role ID for 'user'
            String roleUri = adminBaseUri + "/admin/realms/Donaton/roles/user";
            String roleResponse = restTemplate.exchange(roleUri, HttpMethod.GET, userRequest, String.class).getBody();
            @SuppressWarnings("unchecked")
            Map<String, Object> roleMap = new ObjectMapper().readValue(roleResponse, HashMap.class);
            String roleId = (String) roleMap.get("id");

            // 3. Assign Role
            String mappingUri = adminBaseUri + "/admin/realms/Donaton/users/" + userId + "/role-mappings/realm";
            Map<String, Object> roleAssignment = new HashMap<>();
            roleAssignment.put("name", "user"); // Keycloak accepts role name or ID in some versions, but let's use role ID for precision
            
            // We send a list of role objects
            Map<String, String> roleObj = new HashMap<>();
            roleObj.put("id", roleId);
            
            HttpHeaders postHeaders = new HttpHeaders();
            postHeaders.set("Authorization", "Bearer " + accessToken);
            postHeaders.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<List<Map<String, String>>> postRequest = new HttpEntity<>(List.of(roleObj), postHeaders);
            restTemplate.exchange(mappingUri, HttpMethod.POST, postRequest, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to assign role to user: " + e.getMessage(), e);
        }
    }
>>>>>>> develop
}
