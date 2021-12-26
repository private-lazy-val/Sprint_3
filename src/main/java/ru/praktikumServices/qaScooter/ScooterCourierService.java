package ru.praktikumServices.qaScooter;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.praktikumServices.qaScooter.requests.LoginCourierRequest;
import ru.praktikumServices.qaScooter.requests.RegisterCourierRequest;

import static io.restassured.RestAssured.given;

public class ScooterCourierService {
    @Step("Send POST request to /api/v1/courier")
    public Response registerNewCourierAndReturnResponse(RegisterCourierRequest registerCourierRequest) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(registerCourierRequest.toJsonString())
                .when()
                .post("/api/v1/courier");
    }

    @Step("Login courier and if success return response body")
    public Response loginCourierWithRequestBodyAndReturnResponse(LoginCourierRequest loginCourierRequest) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(loginCourierRequest.toJsonString())
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Send DELETE request to /api/v1/courier")
    public Response deleteCourierAndReturnResponse(String idStr) {

        return given()
                .header("Content-type", "application/json")
                .when()
                .delete("/api/v1/courier/" + idStr);
    }

}
