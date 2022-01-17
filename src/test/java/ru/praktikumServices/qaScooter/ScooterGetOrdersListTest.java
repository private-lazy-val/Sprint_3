package ru.praktikumServices.qaScooter;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.praktikumServices.qaScooter.requests.CancelOrderRequest;
import ru.praktikumServices.qaScooter.requests.CreateOrderRequest;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.notNullValue;

public class ScooterGetOrdersListTest {

    ScooterOrdersService scooterOrdersService;
    List<String> tracks;

    @Before
    public void setUp() {
        scooterOrdersService = new ScooterOrdersService();
        tracks = new ArrayList<>();
    }

    @After
    public void tearDown() {
        for (String track : tracks) {
            scooterOrdersService.cancelOrderAndReturnResponse(new CancelOrderRequest(track));
        }
    }

    @Test
    @DisplayName("Check get orders list")
    @Description("Checking if \"orders\" field is presented in the response and status code is 200")
    public void testGetOrdersListReturnOrdersListAndStatus200() {
        ValidatableResponse newOrderResponse = scooterOrdersService.createNewOrderAndReturnResponse(CreateOrderRequest.getRandom());

        scooterOrdersService.getOrdersListResponse()
                .assertThat().body("orders", notNullValue())
                .and()
                .statusCode(200);

        String track = newOrderResponse
                .assertThat().statusCode(SC_CREATED)
                .extract()
                .body()
                .path("track").toString();

        tracks.add(track);
    }
}
