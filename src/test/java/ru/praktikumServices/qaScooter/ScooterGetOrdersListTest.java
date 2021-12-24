package ru.praktikumServices.qaScooter;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static ru.praktikumServices.qaScooter.Utils.randomString;

public class ScooterGetOrdersListTest {

    ScooterOrdersService scooterOrdersService;
    List<String> tracks;


    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
        scooterOrdersService = new ScooterOrdersService();
        tracks = new ArrayList<>();
    }

    @Test
    @DisplayName("Check get orders list")
    @Description("Checking if \"orders\" field is presented in the response and status code is 200")
    public void testGetOrdersListReturnOrdersListAndStatus200() {
        String createOrderBody = createNewOrderRequestBody();

        Response newOrderResponse = scooterOrdersService.createNewOrderAndReturnResponse(createOrderBody);

        Response response = scooterOrdersService.getOrdersListResponse();
        verifyOrdersListNotNullAndStatusCode200(response);

        verifyOrderCreatedAndAddTrackToListForDeletion(newOrderResponse);
    }

    @Step("Creating new order request body")
    private String createNewOrderRequestBody() {
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
                + "\"comment\":\"" + comment + "\"}";
    }


    @Step("Verify that response contains \"orders\" field which is not null and status code is 200")
    private void verifyOrdersListNotNullAndStatusCode200(Response response) {
        response.then().assertThat().body("orders", notNullValue())
                .and()
                .statusCode(200);
    }

    @Step("Verify that new order was created and add its track to list for further deletion after the test is completed")
    private void verifyOrderCreatedAndAddTrackToListForDeletion(Response newOrderResponse) {
        if (newOrderResponse.statusCode() == 201) {
            String track = newOrderResponse.body().path("track").toString();
            tracks.add(track);
        }
    }

    @After
    public void tearDown() {
        for (String track : tracks) {
            scooterOrdersService.cancelOrderAndReturnResponse(track);
        }
    }

    private String createDateString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return formatter.format(date);
    }
}
