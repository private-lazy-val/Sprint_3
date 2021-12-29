package ru.praktikumServices.qaScooter;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import ru.praktikumServices.qaScooter.requests.CancelOrderRequest;
import ru.praktikumServices.qaScooter.requests.CreateOrderRequest;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ScooterOrdersService extends RestAssuredClient {

    private static final String PATH = "/api/v1/orders/";

    @Step("Send POST request to /api/v1/orders")
    public ValidatableResponse createNewOrderAndReturnResponse(CreateOrderRequest createOrderRequest) {
        return given()
                .log().all()
                .spec(getBaseSpec())
                .body(createOrderRequest.toJsonString())
                .when()
                .post(PATH)
                .then();

    }

    @Step("Send DELETE request to /api/v1/orders/cancel")
    public ValidatableResponse cancelOrderAndReturnResponse(CancelOrderRequest cancelOrderRequest) {
        return given()
                .spec(getBaseSpec())
                .body(cancelOrderRequest.toJsonString())
                .when()
                .delete(PATH + "cancel/")
                .then();
    }

    @Step("Send GET request to /api/v1/orders")
    public ValidatableResponse getOrdersListResponse() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(PATH)
                .then();
    }

    @Step("Send GET request to /api/v1/orders/track and return response")
    public ValidatableResponse getOrderByTrackAndReturnResponse(String trackString) {
        RequestSpecification requestSpecification = given()
                .spec(getBaseSpec())
                .when();
        if (!trackString.isEmpty()) {
            requestSpecification = requestSpecification.queryParam("t", trackString);
        }
        return requestSpecification
                .get(PATH + "track")
                .then();

    }

    @Step("Send PUT request to /api/v1/orders/accept")
    public ValidatableResponse acceptOrderByOrderIdAndCourierId(String orderId, String courierId) {
        String path = PATH + "accept";

        Map<String, String> queryParamMap = new HashMap<>();
        if (!orderId.isEmpty()) {
            queryParamMap.put("id", orderId);
        }
        if (!courierId.isEmpty()) {
            queryParamMap.put("courierId", courierId);
        }

        return given()
                .spec(getBaseSpec())
                .queryParams(queryParamMap)
                .header("Content-type", "application/json")
                .when()
                .put(path)
                .then();
    }


}
