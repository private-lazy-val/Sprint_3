package ru.praktikumServices.qaScooter;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import ru.praktikumServices.qaScooter.requests.CancelOrderRequest;
import ru.praktikumServices.qaScooter.requests.CreateOrderRequest;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ScooterOrdersService {
    @Step("Send POST request to /api/v1/orders")
    public Response createNewOrderAndReturnResponse(CreateOrderRequest createOrderRequest) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(createOrderRequest.toJsonString())
                .when()
                .post("/api/v1/orders");

    }

    @Step("Send DELETE request to /api/v1/orders/cancel")
    public Response cancelOrderAndReturnResponse(CancelOrderRequest cancelOrderRequest) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(cancelOrderRequest.toJsonString())
                .when()
                .delete("/api/v1/orders/cancel/");
    }

    @Step("Send GET request to /api/v1/orders")
    public Response getOrdersListResponse() {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get("/api/v1/orders");
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
                .get("/api/v1/orders/track");

    }

    @Step("Send PUT request to /api/v1/orders/accept")
    public Response acceptOrderByOrderIdAndCourierId(String orderId, String courierId) {
        String path = "/api/v1/orders/accept";

        Map<String, String> queryParamMap = new HashMap<>();
        if (!orderId.isEmpty()) {
            queryParamMap.put("id", orderId);
        }
        if (!courierId.isEmpty()) {
            queryParamMap.put("courierId", courierId);
        }

        return given()
                .log().all()
                .queryParams(queryParamMap)
                .header("Content-type", "application/json")
                .when()
                .put(path);
    }


}
