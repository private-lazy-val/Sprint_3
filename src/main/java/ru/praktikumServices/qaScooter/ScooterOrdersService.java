package ru.praktikumServices.qaScooter;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.text.SimpleDateFormat;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static ru.praktikumServices.qaScooter.Utils.randomString;

public class ScooterOrdersService {
    @Step("Send POST request to /api/v1/orders")
    public Response createNewOrderAndReturnResponse(String createOrderRequestBody) {

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(createOrderRequestBody)
                .when()
                .post("https://qa-scooter.praktikum-services.ru/api/v1/orders");
        return response;

    }

    @Step("Create new order and if success return track")
    public String createNewOrderAndReturnTrack() {
        String newOrderBody = createNewOrderRequestBody("");
        Response response = createNewOrderAndReturnResponse(newOrderBody);
        if (response.statusCode() == 201) {
            return response.body().path("track").toString();
        }
        return "";
    }

    @Step("Send DELETE request to /api/v1/orders/cancel")
    public Response cancelOrderAndReturnResponse(String track) {

        String cancelOrderBody = "{\"track\": " + track + "}";

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(cancelOrderBody)
                .when()
                .delete("http://qa-scooter.praktikum-services.ru/api/v1/orders/cancel/");

        return response;
    }

    @Step("Send GET request to /api/v1/orders")
    public Response getOrdersListResponse() {
        Response response = given()
                .header("Content-type", "application/json")
                .when()
                .get("https://qa-scooter.praktikum-services.ru/api/v1/orders");

        return response;
    }

    @Step("Send GET request to /api/v1/orders/track and return id")
    public String getOrderByTrackAndReturnId(String trackString) {
        Response response = getOrderByTrackAndReturnResponse(trackString);

        if (response.statusCode() == 200) {
            return response.body().path("order.id").toString();
        }
        return "";
    }

    @Step("Send GET request to /api/v1/orders/track and return response")
    public Response getOrderByTrackAndReturnResponse(String trackString) {
        RequestSpecification requestSpecification = given()
                .header("Content-type", "application/json")
                .when();
        if (!trackString.isEmpty()) {
            requestSpecification = requestSpecification.queryParam("t", trackString);
        }
        return requestSpecification
                .get("https://qa-scooter.praktikum-services.ru/api/v1/orders/track");

    }

    @Step("Send PUT request to /api/v1/orders/accept")
    public Response acceptOrderByOrderIdAndCourierId(String orderId, String courierId) {
        String path = "https://qa-scooter.praktikum-services.ru/api/v1/orders/accept/";
        if (!orderId.isEmpty()) {
            path += orderId;
        }
        if (!courierId.isEmpty()) {
            if (!orderId.isEmpty()) {
                path += "?";
            }

            path += "courierId=" + courierId;
        }

        Response response = given()
                .header("Content-type", "application/json")
                .when()
                .put(path);

        return response;
    }

    private String createDateString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return formatter.format(date);
    }

    @Step("Create new order request body")
    public String createNewOrderRequestBody(String colorField) {
        String firstName = randomString();
        String lastName = randomString();
        String address = randomString();
        String metroStation = randomString();
        String phone = randomString();
        int rentTime = new java.util.Random().nextInt();

        String deliveryDate = createDateString();

        String comment = randomString();

        return "{\"firstName\":\"" + firstName + "\","
                + "\"lastName\":\"" + lastName + "\","
                + "\"address\":\"" + address + "\","
                + "\"metroStation\":\"" + metroStation + "\","
                + "\"phone\":\"" + phone + "\","
                + "\"rentTime\":\"" + rentTime + "\","
                + "\"deliveryDate\":\"" + deliveryDate + "\","
                + "\"comment\":\"" + comment + "\""
                + (colorField.isEmpty() ? "" : ", \"color\": " + colorField) + "}";
    }
}
