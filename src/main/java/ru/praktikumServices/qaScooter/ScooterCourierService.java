package ru.praktikumServices.qaScooter;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.praktikumServices.qaScooter.requests.LoginCourierRequest;
import ru.praktikumServices.qaScooter.requests.RegisterCourierRequest;

import static io.restassured.RestAssured.given;

public class ScooterCourierService extends RestAssuredClient {

    private static final String PATH = "/api/v1/courier/";

    @Step("Send POST request to /api/v1/courier")
    public ValidatableResponse registerNewCourierAndReturnResponse(RegisterCourierRequest registerCourierRequest) {
        return given()
                .spec(getBaseSpec())
                .body(registerCourierRequest.toJsonString())
                .when()
                .post(PATH)
                .then();
    }

    @Step("Login courier and if success return response body")
    public ValidatableResponse loginCourierWithRequestBodyAndReturnResponse(LoginCourierRequest loginCourierRequest) {
        return given()
                .spec(getBaseSpec())
                .body(loginCourierRequest.toJsonString())
                .when()
                .post(PATH + "login")
                .then();
    }

    @Step("Send DELETE request to /api/v1/courier")
    public ValidatableResponse deleteCourierAndReturnResponse(String idStr) {

        return given()
                .spec(getBaseSpec())
                .when()
                .delete(PATH + idStr)
                .then();
    }

}
