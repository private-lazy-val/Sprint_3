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
import static ru.praktikumServices.qaScooter.Utils.randomString;

public class ScooterCourierRegisterTest {

    ScooterCourierService scooterCourierService;
    List<String> ids;

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
        scooterCourierService = new ScooterCourierService();
        ids = new ArrayList<>();
    }

    @Test
    @DisplayName("Check register courier")
    @Description("Checking if \"ok\" field has flag \"true\" in the response and status code is 201")
    public void testRegisterNewCourierReturn201True() {
        String login = randomString();
        String password = randomString();
        String firstName = randomString();
        String registerRequestBody = "{\"login\":\"" + login + "\","
                + "\"password\":\"" + password + "\","
                + "\"firstName\":\"" + firstName + "\"}";
        Response response = scooterCourierService.registerNewCourierAndReturnResponse(registerRequestBody);
        verifyOkFieldHasFlagTrueAndStatusCode201(response);

        String id = scooterCourierService.loginAndReturnId(login, password);
        addCouriersIdToListForDeletion(id);
    }


    @Test
    @DisplayName("Check register two couriers with same logins")
    @Description("Checking if \"message\" field has string \"Этот логин уже используется\" in the response and status code is 409")
    @Ignore("No response from server")
    public void testRegisterTwoCouriersWithSameLoginReturn409UserExists() {
        String login = randomString();
        String password = randomString();
        String firstName = randomString();
        String registerRequestBody = "{\"login\":\"" + login + "\","
                + "\"password\":\"" + password + "\","
                + "\"firstName\":\"" + firstName + "\"}";
        scooterCourierService.registerNewCourierAndReturnResponse(registerRequestBody);
        Response response = scooterCourierService.registerNewCourierAndReturnResponse(registerRequestBody);
        verifyLoginDuplicateMessageAndStatusCode409(response);

        String id = scooterCourierService.loginAndReturnId(login, password);
        addCouriersIdToListForDeletion(id);
    }


    @Test
    @DisplayName("Check register courier without password")
    @Description("Checking if \"message\" field has string \"Недостаточно данных для создания учетной записи\" in the response and status code is 400")
    public void testRegisterCourierWithoutPasswordReturn400BadRequest() {
        String login = randomString();
        String firstName = randomString();
        String registerRequestBody = "{\"login\":\"" + login + "\","
                + "\"firstName\":\"" + firstName + "\"}";
        Response response = scooterCourierService.registerNewCourierAndReturnResponse(registerRequestBody);
        verifyBadRequestMessageAndStatusCode400(response);
    }


    @Test
    @DisplayName("Check register courier without login")
    @Description("Checking if \"message\" field has string \"Недостаточно данных для создания учетной записи\" in the response and status code is 400")
    public void testRegisterCourierWithoutLoginReturn400BadRequest() {
        String password = randomString();
        String firstName = randomString();
        String registerRequestBody = "{\"password\":\"" + password + "\","
                + "\"firstName\":\"" + firstName + "\"}";
        Response response = scooterCourierService.registerNewCourierAndReturnResponse(registerRequestBody);
        verifyBadRequestMessageAndStatusCode400(response);
    }

    @Step("Verify that response contains \"ok\" field with flag true and status code is 201")
    private void verifyOkFieldHasFlagTrueAndStatusCode201(Response response) {
        response.then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(201);
    }

    @Step("Verify that response contains field \"message\" with string \"Этот логин уже используется\" and status code is 409")
    private void verifyLoginDuplicateMessageAndStatusCode409(Response response) {
        response.then().assertThat().body("message", equalTo("Этот логин уже используется"))
                .and()
                .statusCode(409);
    }

    @Step("Verify that response contains field \"message\" with string \"Недостаточно данных для создания учетной записи\" and status code is 400")
    private void verifyBadRequestMessageAndStatusCode400(Response response) {
        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400);
    }

    @Step("Add courier's id to list for further deletion after the test is completed")
    private void addCouriersIdToListForDeletion(String id) {
        ids.add(id);
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






