package ru.praktikumServices.qaScooter;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.praktikumServices.qaScooter.requests.CancelOrderRequest;
import ru.praktikumServices.qaScooter.requests.CreateOrderRequest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

public class ScooterGetOrdersListTest {

    ScooterOrdersService scooterOrdersService;
    List<String> tracks;

    @Before
    public void setUp() {
        RestAssured.baseURI = Utils.baseURI;
        scooterOrdersService = new ScooterOrdersService();
        tracks = new ArrayList<>();
    }

    @Test
    @DisplayName("Check get orders list")
    @Description("Checking if \"orders\" field is presented in the response and status code is 200")
    public void testGetOrdersListReturnOrdersListAndStatus200() {
        Response newOrderResponse = scooterOrdersService.createNewOrderAndReturnResponse(new CreateOrderRequest());

        scooterOrdersService.getOrdersListResponse()
                .then().assertThat().body("orders", notNullValue())
                .and()
                .statusCode(200);

        String track = newOrderResponse.then()
                .assertThat().statusCode(201)
                .extract()
                .body()
                .path("track").toString();

        tracks.add(track);
    }

    @After
    public void tearDown() {
        for (String track : tracks) {
            scooterOrdersService.cancelOrderAndReturnResponse(new CancelOrderRequest(track));
        }
    }
}
