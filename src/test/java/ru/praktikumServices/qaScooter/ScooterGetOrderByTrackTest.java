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

public class ScooterGetOrderByTrackTest {
    ScooterOrdersService scooterOrdersService;

    List<String> orderIds;

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
        scooterOrdersService = new ScooterOrdersService();

        orderIds = new ArrayList<>();
    }

    @DisplayName("Check get order by track")
    @Description("Checking if order field is not null in the response and status code is 200")
    @Test
    public void testGetOrderByTrackReturn200() {
        String orderTrack = scooterOrdersService.createNewOrderAndReturnTrack();
        verifyGetOrderSuccessAndStatusCode200(orderTrack);
        String orderId = scooterOrdersService.getOrderByTrackAndReturnId(orderTrack);
        orderIds.add(orderId);
    }

    @DisplayName("Check get order by wrong track")
    @Description("Checking if \"message\" field has string \"Заказ не найден\" and status code is 404")
    @Test
    @Ignore("Can't ensure order id doesn't exist by deleting existing order: delete order api method doesn't work")
    public void testGetOrderWithWrongTrackReturn404() {
        String orderTrack = scooterOrdersService.createNewOrderAndReturnTrack();
        scooterOrdersService.cancelOrderAndReturnResponse(orderTrack);
        verifyOrderNotFoundAndStatusCode404(orderTrack);
    }

    @DisplayName("Check get order without track")
    @Description("Checking if \"message\" field has string \"Недостаточно данных для поиска\" and status code is 400")
    @Test
    public void testGetOrderWithoutTrackReturn400() {
        verifyBadRequestAndStatusCode400("");
    }

    @After
    public void tearDown() {
        for(String orderId: orderIds) {
            scooterOrdersService.cancelOrderAndReturnResponse(orderId);
        }
    }


    @Step("Verify that order was listed and status code is 200")
    private void verifyGetOrderSuccessAndStatusCode200(String orderTrack) {
        Response response = scooterOrdersService.getOrderByTrackAndReturnResponse(orderTrack);
        response.then().assertThat().body("order", notNullValue())
                .and()
                .statusCode(200);
    }

    @Step("Verify that order was not found and status code is 404")
    private void verifyOrderNotFoundAndStatusCode404(String orderTrack) {
        Response response = scooterOrdersService.getOrderByTrackAndReturnResponse(orderTrack);
        response.then().assertThat().body("message", equalTo("Заказ не найден"))
                .and()
                .statusCode(404);
    }

    @Step("Verify that order can't be found because track is missing and status code is 400")
    private void verifyBadRequestAndStatusCode400(String orderTrack) {
        Response response = scooterOrdersService.getOrderByTrackAndReturnResponse(orderTrack);
        response.then().assertThat().body("message", equalTo("Недостаточно данных для поиска"))
                .and()
                .statusCode(400);
    }
}
