package com.daggerok.oauth2;

import com.daggerok.oauth2.data.Domain;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.daggerok.oauth2.SecureMePleaseApplicationTests.accessTokenValiditySeconds;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by mak on 4/30/16.
 */
@Slf4j
@WebAppConfiguration
@IntegrationTest(value = {
        // run server on random port
        "server.port=0",
        // decrease token expiration time to 3 seconds
        "app.access-token-validity-seconds=" + accessTokenValiditySeconds})
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SecureMePleaseApplication.class)
public class SecureMePleaseApplicationTests {

    static final int accessTokenValiditySeconds = 3;

    /* // useless
    @Value("${local.server.port}")
    int port;
    */

    @Value("${spring.data.rest.base-path}")
    String api;

    @Autowired WebApplicationContext wac;

    @Autowired Filter springSecurityFilterChain;

    MockMvc mockMvc;

    String tokenUri, domainUri;

    ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {

        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        tokenUri = "/oauth/token";
        domainUri = api + "/domains";
        mockMvc = webAppContextSetup(wac)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    @Test
    @SneakyThrows
    public void testUnauthorizedGet() {

        mockMvc.perform(get(domainUri))
                .andExpect(status().isUnauthorized());
    }

    private MockHttpServletRequest correctCredentials(MockHttpServletRequest request) {

        request.setParameter("username", "max");
        request.setParameter("password", "pass");
        request.setParameter("grant_type", "password");
        return request;
    }

    @Test
    @SneakyThrows
    public void testPostOauthToken() { // see token() method at the bottom of the file

        MvcResult result = mockMvc
                .perform(authToken(tokenUri)
                        .with(user("rest-user")
                                .password("cli"))
                        .with(this::correctCredentials))
                .andExpect(res -> // verify correct amount of seconds left...
                        assertEquals(accessTokenValiditySeconds - 1, parse(res).get("expires_in")))
                .andReturn();
        Map tokenData = parse(result);

        print(result);
        assertTrue(tokenData.containsKey("access_token"));
        assertTrue(tokenData.containsKey("token_type"));
        assertTrue(tokenData.containsKey("refresh_token"));
        assertTrue(tokenData.containsKey("expires_in"));
        assertTrue(tokenData.containsKey("scope"));
    }

    @Test
    @SneakyThrows
    public void testAuthorizedGet() {

        ResultActions result = mockMvc.perform(get(domainUri)
                .header("authorization", token()))
                .andExpect(status().isOk());
        Map response = parse(result.andReturn());

        log.info(ToStringBuilder.reflectionToString(response));
    }

    @Test
    @SneakyThrows
    public void testAuthorizedPostDomain() { // see postNewDomain(token) method on the end of file

        MvcResult result = mockMvc
                .perform(post(domainUri)
                        .header("authorization", token())
                        .content(mapper.writeValueAsBytes(Domain.of("some content")))
                        .contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andReturn();

        print(result);
        assertEquals("", result.getResponse().getContentAsString());
    }

    @Test
    @SneakyThrows
    public void testExpiration() {

        String token = token();
        MvcResult success = postNewDomain(token, Domain.of("one"))
                .andExpect(status().isCreated())
                .andReturn();

        print(success);
        TimeUnit.SECONDS.sleep(accessTokenValiditySeconds);

        MvcResult failed = postNewDomain(token, Domain.of("two"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        print(failed);

        Map actual = parse(failed);
        String expiredToken = token.replace("bearer ", "");

        assertTrue(actual.containsKey("error"));
        assertTrue(actual.containsKey("error_description"));

        assertEquals("invalid_token", actual.get("error"));
        assertEquals(format("Access token expired: %s", expiredToken), actual.get("error_description"));
    }

    @SneakyThrows
    private void print(MvcResult result) {
        log.info(result.getResponse().getContentAsString());
    }

    @SneakyThrows
    private static Map parse(MvcResult result) {
        return new ObjectMapper().readValue(result.getResponse().getContentAsString(), Map.class);
    }

    public static MockHttpServletRequestBuilder authToken(String tokenUri) {
        return post(tokenUri);
    }

    @SneakyThrows
    String token() {

        return "bearer ".concat(String.valueOf(parse(mockMvc
                .perform(authToken(tokenUri)
                        .with(user("rest-user")
                                .password("cli"))
                        .with(this::correctCredentials))
                .andExpect(res -> // verify correct amount of seconds left...
                        assertEquals(accessTokenValiditySeconds - 1, parse(res).get("expires_in")))
                .andReturn()).get("access_token")));
    }

    @SneakyThrows
    ResultActions postNewDomain(String token, Domain domain) {

        return mockMvc
                .perform(post(domainUri)
                        .header("authorization", token)
                        .content(mapper.writeValueAsBytes(domain))
                        .contentType(APPLICATION_JSON_VALUE));
    }
}
