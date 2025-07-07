package com.nolions.helloword.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class HellowordStepDefs {
    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<String> response;

    @LocalServerPort
    private int port;


    @Given("server running")
    public void serviceRunning() {
        // 預設 SpringBootTest 會啟動應用程式
    }

    @When("call {string} API")
    public void callApi(String path) {
        String baseUrl = "http://localhost:" + port + "/" + path;
        response = restTemplate.getForEntity(baseUrl, String.class);
    }

    @Then("get {string} response")
    public void verifyResponse(String expected) {
        assertThat(response.getBody()).isEqualTo(expected);
    }
}
