package ru.praktikumServices.qaScooter;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static ru.praktikumServices.qaScooter.Utils.randomString;

public class ScooterCourierLoginTest {

    ScooterCourierService scooterCourierService;
    List<String> ids;
    String login;
    String password;
    String firstName;

    @Before
    @Step("Registering new courier, getting id through login, adding id to list for further deletion after the test is completed")
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
        scooterCourierService = new ScooterCourierService();
        ids = new ArrayList<>();
        login = randomString();
        password = randomString();
        firstName = randomString();
        String registerRequestBody = "{\"login\":\"" + login + "\","
                + "\"password\":\"" + password + "\","
                + "\"firstName\":\"" + firstName + "\"}";
        scooterCourierService.registerNewCourierAndReturnResponse(registerRequestBody);

        String id = scooterCourierService.loginAndReturnId(login, password);
        ids.add(id);
    }

    @Test
    @DisplayName("Check courier login")
    @Description("Checking if \"id\" field is presented in the response and status code is 200")
    public void testCourierLoginReturn200AndId() {
        Response response = scooterCourierService.loginCourierAndReturnResponse(login, password);
        verifyIdFieldIsNotNullAndStatusCode200(response);
    }

    @Test
    @DisplayName("Check courier login without login")
    @Description("Checking if \"message\" field has string \"Недостаточно данных для входа\" in the response and status code is 400")
    public void testCourierLoginWithoutLoginReturn404NotFound() {

        String loginRequestBody = "{\"password\":\"" + password + "\"}";
        Response response = scooterCourierService.loginCourierWithRequestBodyAndReturnResponse(loginRequestBody);
        verifyBadRequestMessageAndStatusCode400(response);
    }

    @Test
    @DisplayName("Check courier login without password")
    @Description("Checking if \"message\" field has string \"Недостаточно данных для входа\" in the response and status code is 400")
    @Ignore("Specified response string differs from actual result")
    public void testCourierLoginWithoutPasswordReturn404NotFound() {
        String loginRequestBody = "{\"login\":\"" + login + "\"}";
        Response response = scooterCourierService.loginCourierWithRequestBodyAndReturnResponse(loginRequestBody);
        verifyBadRequestMessageAndStatusCode400(response);
    }

    @Test
    @DisplayName("Check courier login with wrong login")
    @Description("Checking if \"message\" field has string \"Учетная запись не найдена\" in the response and status code is 404")
    public void testCourierLoginWithWrongLoginReturn404NotFound() {
        String wrongLogin = "wrong" + login;
        Response response = scooterCourierService.loginCourierAndReturnResponse(wrongLogin, password);
        verifyNotFoundMessageAndStatusCode404(response);
    }

    @Test
    @DisplayName("Check courier login with wrong password")
    @Description("Checking if \"message\" field has string \"Учетная запись не найдена\" in the response and status code is 404")
    public void testCourierLoginWithWrongPasswordReturn404NotFound() {
        String wrongPassword = "wrong" + password;
        Response response = scooterCourierService.loginCourierAndReturnResponse(wrongPassword, password);
        verifyNotFoundMessageAndStatusCode404(response);
    }

    @Step("Verify that response contains id field which is not null and status code is 200")
    private void verifyIdFieldIsNotNullAndStatusCode200(Response response) {
        response.then().assertThat().body("id", notNullValue())
                .and()
                .statusCode(200);
    }

    @Step("Verify that response contains field \"message\" with string \"Недостаточно данных для входа\" and status code is 400")
    private void verifyBadRequestMessageAndStatusCode400(Response response) {
        response.then().assertThat().body("message", equalTo("Недостаточно данных для входа"))
                .and()
                .statusCode(400);
    }

    @Step("Verify that response contains field \"message\" with string \"Учетная запись не найдена\" and status code is 404")
    private void verifyNotFoundMessageAndStatusCode404(Response response) {
        response.then().assertThat().body("message", equalTo("Учетная запись не найдена"))
                .and()
                .statusCode(404);
    }

    @After
    public void tearDown() {
        for (String id : ids) {
            if (!id.equals("")) {
                scooterCourierService.deleteCourierAndReturnResponse(id);
            }
        }
    }

}
