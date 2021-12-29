package ru.praktikumServices.qaScooter;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;

import org.junit.Test;
import ru.praktikumServices.qaScooter.requests.CancelOrderRequest;
import ru.praktikumServices.qaScooter.requests.CreateOrderRequest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class ScooterGetOrderByTrackTest {
    ScooterOrdersService scooterOrdersService;

    List<String> orderIds;

    @Before
    public void setUp() {
        scooterOrdersService = new ScooterOrdersService();

        orderIds = new ArrayList<>();
    }

    @After
    public void tearDown() {
        for (String orderId : orderIds) {
            scooterOrdersService.cancelOrderAndReturnResponse(new CancelOrderRequest(orderId));
        }
    }

    @DisplayName("Check get order by track")
    @Description("Checking if order field is not null in the response and status code is 200")
    @Test
    public void testGetOrderByTrackReturn200() {
        String orderTrack = scooterOrdersService.createNewOrderAndReturnResponse(CreateOrderRequest.getRandom())
                .assertThat().statusCode(201)
                .extract()
                .body()
                .path("track").toString();
        scooterOrdersService
                .getOrderByTrackAndReturnResponse(orderTrack).assertThat().body("order", notNullValue())
                .and()
                .statusCode(200);
        String orderId = scooterOrdersService.getOrderByTrackAndReturnResponse(orderTrack).assertThat().statusCode(200)
                .extract()
                .body()
                .path("order.id").toString();
        orderIds.add(orderId);
    }

    // Can't ensure order id doesn't exist by deleting existing order: delete order api method doesn't work

    @DisplayName("Check get order by wrong track")
    @Description("Checking if \"message\" field has string \"Заказ не найден\" and status code is 404")
    @Test
    public void testGetOrderWithWrongTrackReturn404() {
        String orderTrack = scooterOrdersService.createNewOrderAndReturnResponse(CreateOrderRequest.getRandom())
                .assertThat().statusCode(201)
                .extract()
                .body()
                .path("track").toString();
        scooterOrdersService.cancelOrderAndReturnResponse(new CancelOrderRequest(orderTrack));
        scooterOrdersService.getOrderByTrackAndReturnResponse(orderTrack)
                .assertThat().body("message", equalTo("Заказ не найден"))
                .and()
                .statusCode(404);
    }


}
