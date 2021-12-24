package ru.praktikumServices.qaScooter;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;

public class ScooterCourierService {

    @Step("Send POST request to /api/v1/courier")
    public Response registerNewCourierAndReturnResponse(String registerRequestBody) {

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(registerRequestBody)
                .when()
                .post("https://qa-scooter.praktikum-services.ru/api/v1/courier");
        return response;
    }

    @Step("Login courier and if success return courier's id")
    public String loginAndReturnId(String login, String password) {
        Response response = loginCourierAndReturnResponse(login, password);
        if (response.statusCode() == 200) {
            return response.body().path("id").toString();
        }
        return "";
    }

    public Response loginCourierAndReturnResponse(String login, String password) {

        String loginRequestBody = "{\"login\":\"" + login + "\","
                + "\"password\":\"" + password + "\"}";

        return loginCourierWithRequestBodyAndReturnResponse(loginRequestBody);
    }

    @Step("Login courier and if success return response body")
    public Response loginCourierWithRequestBodyAndReturnResponse(String loginRequestBody) {
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(loginRequestBody)
                .when()
                .post("http://qa-scooter.praktikum-services.ru/api/v1/courier/login");
        return response;
    }

    public Response deleteCourierAndReturnResponse(int id) {
        return deleteCourierAndReturnResponse("" + id);
    }

    @Step("Send DELETE request to /api/v1/courier")
    public Response deleteCourierAndReturnResponse(String idStr) {
        Response response = given()
                .header("Content-type", "application/json")
                .when()
                .delete("http://qa-scooter.praktikum-services.ru/api/v1/courier/" + idStr);

        return response;
    }

}
