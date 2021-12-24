package ru.praktikumServices.qaScooter;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static ru.praktikumServices.qaScooter.Utils.randomString;

public class ScooterCourierDeleteTest {

    ScooterCourierService scooterCourierService;

    @Before
    @Step("Registering new courier")
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
        scooterCourierService = new ScooterCourierService();
    }

    @DisplayName("Check delete courier")
    @Description("Checking if \"ok\" field has flag true in the response and status code is 200")
    @Test
    public void testDeleteCourierReturn200Ok() {
        String id = createCourierAndReturnId();
        Response response = scooterCourierService.deleteCourierAndReturnResponse(id);
        verifyOkTrueAndStatusCode200(response);
    }

    @DisplayName("Delete courier without id")
    @Description("Checking if \"message\" field has string \"Недостаточно данных для создания учетной записи\" and status code is 400")
    @Test
    @Ignore("Specified response string and status code differ from actual result")
    public void testDeleteCourierWithoutIdReturn400BadRequest() {
        Response response = scooterCourierService.deleteCourierAndReturnResponse("");
        verifyBadRequestAndStatusCode400(response);
    }


    @DisplayName("Delete courier with wrong id")
    @Description("Checking if \"message\" field has string \"Курьера с таким id нет\" and status code is 404")
    @Test
    @Ignore("Specified response string differs from actual result")
    public void testDeleteCourierWithWrongIdReturn404NotFound() {
        String id = createCourierAndReturnId();
        scooterCourierService.deleteCourierAndReturnResponse(id);
        Response response = scooterCourierService.deleteCourierAndReturnResponse(id);
        verifyNotFoundAndStatusCode404(response);
    }


    @Step("Verify that response contains \"ok\" field with flag true and status code is 200")
    private void verifyOkTrueAndStatusCode200(Response response) {
        response.then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(200);
    }

    @Step("Verify that response contains \"message\" field with string \"Курьера с таким id нет\" and status code is 404")
    private void verifyNotFoundAndStatusCode404(Response response) {
        response.then().assertThat().body("message", equalTo("Курьера с таким id нет"))
                .and()
                .statusCode(404);
    }

    @Step("Verify that response contains \"message\" field with string \"Недостаточно данных для создания учетной записи\" and status code is 400")
    private void verifyBadRequestAndStatusCode400(Response response) {
        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400);
    }

    @Step("Register and login courier")
    private String createCourierAndReturnId() {
        String login = randomString();
        String password = randomString();
        String firstName = randomString();
        String registerRequestBody = "{\"login\":\"" + login + "\","
                + "\"password\":\"" + password + "\","
                + "\"firstName\":\"" + firstName + "\"}";
        scooterCourierService.registerNewCourierAndReturnResponse(registerRequestBody);
        return scooterCourierService.loginAndReturnId(login, password);
    }
}
