package org.tyaa.demo.springboot.simplespa.junit.controller;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.tyaa.demo.springboot.simplespa.SpringbootSimplespaApplication;
import org.tyaa.demo.springboot.simplespa.controller.AuthController;
import org.tyaa.demo.springboot.simplespa.security.HibernateWebAuthProvider;
import org.tyaa.demo.springboot.simplespa.security.RestAuthenticationEntryPoint;
import org.tyaa.demo.springboot.simplespa.security.SavedReqAwareAuthSuccessHandler;
import org.tyaa.demo.springboot.simplespa.security.SecurityConfig;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = SpringbootSimplespaApplication.class
)
@AutoConfigureMockMvc
@ContextConfiguration(classes = {
        HibernateWebAuthProvider.class,
        RestAuthenticationEntryPoint.class,
        SavedReqAwareAuthSuccessHandler.class,
        SecurityConfig.class
})
@AutoConfigureTestDatabase(replace = NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SecurityControllerMethodsTest {


    private HttpHeaders loginAdmin() {
        return login("admin", "AdminPassword1");
    }

    private HttpHeaders loginUser() {
        return login("two", "UserPassword2");
    }

    private HttpHeaders login(String username, String password) {
        MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
        request.set("username", username);
        request.set("password", password);
        ResponseEntity<String> response =
                this.testRestTemplate.withBasicAuth(username, password)
                        .postForEntity("/login", request, String.class);
        HttpHeaders headers = response.getHeaders();
        String cookie = headers.getFirst(HttpHeaders.SET_COOKIE);
        HttpHeaders requestHttpHeaders = new HttpHeaders();
        requestHttpHeaders.set("Cookie", cookie);
        return requestHttpHeaders;
    }


    @BeforeAll
    static void setup() {
        System.out.println("======================Controller Test Started=============");
    }

    @AfterAll
    static void done() {
        System.out.println("======================Controller Test Finished=============");
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    public AuthController authController;

    final String baseUrl = "http://localhost:" + 8090 + "/simplespa/";

    //проверка входа с правильной комбинацией
    @Test
    public void performLoginWithAdminUserPassword() throws Exception {
        mvc.perform(formLogin("/login")
                .user("admin")
                .password("AdminPassword1"))
                .andExpect((status().isOk()));
    }

    //проверка входа с неправильной комбинацией
    @Test
    public void performLoginWithAdminUserPasswordIncorrect() throws Exception {
        mvc.perform(formLogin("/login")
                .user("admin")
                .password("incorrectPassword"))
                .andExpect((status().is(302)));
    }

    //проверка запроса который не должен пройти
    @Test
    public void whenLoggedUserRequestsAllRolesThenForbidden() throws Exception {
        ResponseEntity<String> response =
                testRestTemplate.exchange(
                        baseUrl + "api/auth/admin/roles",
                        HttpMethod.GET,
                        new HttpEntity<String>(loginUser()),
                        String.class
                );
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    //проверка запроса который должен пройти
    @Test
    public void whenLoggedUserRequestsAllRolesThenOk() throws Exception {
        ResponseEntity<String> response =
                testRestTemplate.exchange(
                        baseUrl + "api/auth/admin/roles",
                        HttpMethod.GET,
                        new HttpEntity<String>(loginAdmin()),
                        String.class
                );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    //проверка выхода из аккаунта
    @Test
    public void performLogout() throws Exception {
        mvc.perform(get("/logout"))
                .andExpect((redirectedUrl("/api/auth/user/signedout")));
    }

}
