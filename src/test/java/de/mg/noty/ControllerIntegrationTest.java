package de.mg.noty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.mg.noty.controller.content.NoteDto;
import de.mg.noty.controller.delta.req.AllContentDto;
import de.mg.noty.controller.delta.req.NoteDeltaDto;
import de.mg.noty.controller.delta.req.NoteTagDeltaDto;
import de.mg.noty.controller.delta.req.TagDeltaDto;
import de.mg.noty.controller.delta.res.ResponseDto;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class ControllerIntegrationTest {

    @ConfigProperty(name = "BASICAUTH_PASSWORD", defaultValue = "12345")
    String password = "12345";

    @Test
    public void testFlow() {

        ResponseDto response = given()
                .auth().preemptive().basic("noty", password)
                .queryParam("lastReceivedServerDelta", 0)
                .when().get("/delta/deltas")
                .then()
                .statusCode(200)
                .extract().as(ResponseDto.class);
        assertEquals(ResponseDto.builder().build(), response);

        response = given()
                .auth().preemptive().basic("noty", password)
                .queryParam("lastReceivedServerDelta", 0)
                .body(json(TagDeltaDto.builder().tagId("1").name("name 1").updated(1L).build()))
                .contentType(MediaType.APPLICATION_JSON)
                .when().post("/delta/tag")
                .then()
                .statusCode(200)
                .extract().as(ResponseDto.class);
        assertEquals(ResponseDto.builder().saved(true).build(), response);

        response = given()
                .auth().preemptive().basic("noty", password)
                .queryParam("lastReceivedServerDelta", 1)
                .body(json(NoteDeltaDto.builder().noteId("1").text("text 1").updated(1L).build()))
                .contentType(MediaType.APPLICATION_JSON)
                .when().post("/delta/note")
                .then()
                .statusCode(200)
                .extract().as(ResponseDto.class);
        assertEquals(ResponseDto.builder().saved(true).build(), response);

        response = given()
                .auth().preemptive().basic("noty", password)
                .queryParam("lastReceivedServerDelta", 2)
                .body(json(NoteTagDeltaDto.builder().noteId("1").tagId("1").updated(2L).build()))
                .contentType(MediaType.APPLICATION_JSON)
                .when().post("/delta/notetag")
                .then()
                .statusCode(200)
                .extract().as(ResponseDto.class);
        assertEquals(ResponseDto.builder().saved(true).build(), response);

        response = given()
                .auth().preemptive().basic("noty", password)
                .queryParam("lastReceivedServerDelta", 0)
                .when().get("/delta/deltas")
                .then()
                .statusCode(200)
                .extract().as(ResponseDto.class);
        assertEquals(3, response.getNewDeltas().size());

        NoteDto[] notesResponse = given()
                .auth().preemptive().basic("noty", password)
                .when().get("/content/notes")
                .then()
                .statusCode(200)
                .extract().as(NoteDto[].class);
        assertTrue(notesResponse != null && notesResponse.length == 1);

        given()
                .auth().preemptive().basic("noty", password)
                .queryParam("lastReceivedServerDelta", 2)
                .body(json(AllContentDto.builder()
                        .noteCreateDeltas(singletonList(NoteDeltaDto.builder()
                                .noteId("111")
                                .text("test")
                                .updated(10L)
                                .build()))
                        .build()))
                .contentType(MediaType.APPLICATION_JSON)
                .when().post("/delta/all")
                .then()
                .statusCode(204);
    }

    private String json(Object o) {
        try {
            return new ObjectMapper().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
