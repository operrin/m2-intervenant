package org.miage.intervenantservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miage.intervenantservice.boundary.IntervenantResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.when;
import org.apache.http.HttpStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class IntervenantServiceApplicationTests {

	@LocalServerPort
	int port;
	@Autowired
	IntervenantResource ir;
	
	@BeforeEach
	void setupContext() {
		ir.deleteAll();
		RestAssured.port = port;
	}

	@Test
	void pingApi() {
		when().get("/intervenants").then().statusCode(HttpStatus.SC_OK);
	}
}
