package com.easyservice.backend.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class DiscoveryServiceTest {

    @Test
    void searchesOneCategoryWithTheExpectedLocationPayload() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://discovery.test");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        RestClient client = builder.build();
        server.expect(requestTo("http://discovery.test/api/discovery/addis/search"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"city\":\"Addis Ababa\",\"area\":\"Bole\",\"category\":\"HOTEL\"}"))
                .andRespond(withSuccess("[{\"id\":\"hotel-1\"}]", MediaType.APPLICATION_JSON));

        Object result = new DiscoveryService(client).search("Addis Ababa", "Bole", "HOTEL");

        assertEquals(1, ((java.util.List<?>) result).size());
        server.verify();
    }

    @Test
    void searchesAllSupportedCategories() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://discovery.test");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        RestClient client = builder.build();
        for (String category : new String[] { "HOTEL", "CAR", "STORE", "EVENT" }) {
            server.expect(requestTo("http://discovery.test/api/discovery/addis/search"))
                    .andExpect(method(HttpMethod.POST))
                    .andExpect(content().json("{\"city\":\"Hawassa\",\"area\":\"ALL\",\"category\":\"" + category + "\"}"))
                    .andRespond(withSuccess("[{\"category\":\"" + category + "\"}]", MediaType.APPLICATION_JSON));
        }

        Object result = new DiscoveryService(client).search("Hawassa", "ALL", "ALL");

        assertEquals(4, ((java.util.List<?>) result).size());
        server.verify();
    }
}