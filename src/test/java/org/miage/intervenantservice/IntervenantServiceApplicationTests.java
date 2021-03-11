package org.miage.intervenantservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miage.intervenantservice.boundary.IntervenantResource;
import org.miage.intervenantservice.entity.Intervenant;
import org.miage.intervenantservice.entity.IntervenantInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

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

	@Test
	public void getAllApi() {
		Intervenant i1 = new Intervenant(UUID.randomUUID().toString(), "Sawyer", "Tom", "Nancy", "54000");
		ir.save(i1);
		Intervenant i2 = new Intervenant(UUID.randomUUID().toString(), "Blanc", "Rovbert", "Les Arcs", "73000");
		ir.save(i2);
		when().get("/intervenants").then().statusCode(HttpStatus.SC_OK)
			.and().assertThat().body("size()", equalTo(2));
	}

	@Test
	public void getOneApi() {
		Intervenant i1 = new Intervenant(UUID.randomUUID().toString(), "Sawyer", "Tom", "Nancy", "54000");
		ir.save(i1);
		Response response = when().get("/intervenants/"+i1.getId())
			.then().statusCode(HttpStatus.SC_OK).extract().response();
		String jsonAsString = response.asString();
		assertThat(jsonAsString, containsString("Tom"));
	}

	@Test
	public void getNotFoundApi() {
		when().get("intervenants/125").then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void postApi() throws Exception {
		IntervenantInput i1 = new IntervenantInput("Sawyer","Tom","Nancy","54000");
		Response response = given().body(toJsonString(i1)).contentType(ContentType.JSON).when()
			.post("intervenants").then().statusCode(HttpStatus.SC_CREATED).extract().response();
		String location = response.getHeader("Location");
		when().get(location).then().statusCode(HttpStatus.SC_OK);
	}

	private String toJsonString(Object r) throws Exception {
		ObjectMapper map = new ObjectMapper();
		return map.writeValueAsString(r);
	}
}
