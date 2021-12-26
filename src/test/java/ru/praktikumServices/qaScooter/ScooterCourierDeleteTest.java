package ru.praktikumServices.qaScooter;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;

import org.junit.Test;
import ru.praktikumServices.qaScooter.requests.LoginCourierRequest;
import ru.praktikumServices.qaScooter.requests.RegisterCourierRequest;

import static org.hamcrest.Matchers.equalTo;

public class ScooterCourierDeleteTest {

    ScooterCourierService scooterCourierService;

    @Before
    @Step("Registering new courier")
    public void setUp() {
        RestAssured.baseURI = Utils.baseURI;
        scooterCourierService = new ScooterCourierService();
    }

    @DisplayName("Check delete courier")
    @Description("Checking if \"ok\" field has flag true in the response and status code is 200")
    @Test
    public void testDeleteCourierReturn200Ok() {
        RegisterCourierRequest registerCourierRequest = new RegisterCourierRequest();
        scooterCourierService.registerNewCourierAndReturnResponse(registerCourierRequest)
                .then()
                .assertThat()
                .statusCode(201);
        LoginCourierRequest loginCourierRequest = new LoginCourierRequest(registerCourierRequest.login, registerCourierRequest.password);
        String id = scooterCourierService.loginCourierWithRequestBodyAndReturnResponse(loginCourierRequest).then().
                assertThat().statusCode(200).
                extract().body().path("id").toString();
        Response response = scooterCourierService.deleteCourierAndReturnResponse(id);
        response.then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(200);
    }

    // Specified response string and status code differ from actual result
    @DisplayName("Delete courier without id")
    @Description("Checking if \"message\" field has string \"Недостаточно данных для создания учетной записи\" and status code is 400")
    @Test
    public void testDeleteCourierWithoutIdReturn400BadRequest() {
        scooterCourierService.deleteCourierAndReturnResponse("")
                .then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400);
    }


    // Specified response string differs from actual result
    @DisplayName("Delete courier with wrong id")
    @Description("Checking if \"message\" field has string \"Курьера с таким id нет\" and status code is 404")
    @Test
    public void testDeleteCourierWithWrongIdReturn404NotFound() {
        RegisterCourierRequest registerCourierRequest = new RegisterCourierRequest();
        scooterCourierService.registerNewCourierAndReturnResponse(registerCourierRequest)
                .then()
                .assertThat()
                .statusCode(201);
        LoginCourierRequest loginCourierRequest = new LoginCourierRequest(registerCourierRequest.login, registerCourierRequest.password);
        String id = scooterCourierService.loginCourierWithRequestBodyAndReturnResponse(loginCourierRequest).then().
                assertThat().statusCode(200).
                extract().body().path("id").toString();
        scooterCourierService.deleteCourierAndReturnResponse(id)
                .then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(200);
        scooterCourierService.deleteCourierAndReturnResponse(id)
                .then().assertThat().body("message", equalTo("Курьера с таким id нет"))
                .and()
                .statusCode(404);
    }
}
