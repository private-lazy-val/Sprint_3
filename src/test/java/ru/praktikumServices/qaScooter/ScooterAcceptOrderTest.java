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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static ru.praktikumServices.qaScooter.Utils.randomString;

public class ScooterAcceptOrderTest {
    ScooterOrdersService scooterOrdersService;
    ScooterCourierService scooterCourierService;

    List<String> courierIds;
    List<String> orderIds;

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
        scooterOrdersService = new ScooterOrdersService();
        scooterCourierService = new ScooterCourierService();

        courierIds = new ArrayList<>();
        orderIds = new ArrayList<>();
    }

    @DisplayName("Check accept order")
    @Description("Checking if \"ok\" field has flag true in the response and status code is 200")
    @Test
    public void testAcceptOrderReturn200() {
        String orderId = createNewOrderAndReturnId();

        String courierId = registerCourierAndReturnId();

        verifyOrderAcceptedAndStatusCode200(orderId, courierId);
        courierIds.add(courierId);
        orderIds.add(orderId);
    }


    @DisplayName("Check accept order without courier id")
    @Description("Checking if \"message\" field has string \"Недостаточно данных для поиска\" in the response and status code is 400")
    @Test
    public void testAcceptOrderWithoutCourierIdReturn400() {
        String orderId = createNewOrderAndReturnId();
        verifyConflictErrorAndStatusCode400(orderId, "");
        orderIds.add(orderId);
    }


    @DisplayName("Check accept order without order id")
    @Description("Checking if \"message\" field has string \"Недостаточно данных для поиска\" in the response  and status code is 400")
    @Test
    public void testAcceptOrderWithoutOrderIdReturn400() {
        String courierId = registerCourierAndReturnId();
        verifyConflictErrorAndStatusCode400("", courierId);
        courierIds.add(courierId);
    }

    @DisplayName("Check accept order with wrong courier id")
    @Description("Checking if \"message\" field has string \"Курьера с таким id не существует\" in the response  and status code is 404")
    @Test
    public void testAcceptOrderWithWrongCourierIdReturn404() {
        String courierId = registerCourierAndReturnId();
        scooterCourierService.deleteCourierAndReturnResponse(courierId);

        String orderId = createNewOrderAndReturnId();
        verifyCourierNotFoundAndStatusCode404(orderId, courierId);
        orderIds.add(orderId);
    }

    @DisplayName("Check accept order with wrong order id")
    @Description("Checking if \"message\" field has string \"Заказа с таким id не существует\" in the response  and status code is 404")
    @Ignore("Can't ensure order id doesn't exist by deleting existing order: delete order api method doesn't work")
    @Test
    public void testAcceptOrderWithWrongOrderIdReturn404() {
        String courierId = registerCourierAndReturnId();
        String orderTrack = scooterOrdersService.createNewOrderAndReturnTrack();
        String orderId = scooterOrdersService.getOrderByTrackAndReturnId(orderTrack);
        scooterOrdersService.cancelOrderAndReturnResponse(orderTrack);

        verifyOrderNotFoundAndStatusCode404(orderId, courierId);
        courierIds.add(courierId);
    }

    @After
    public void tearDown() {
        for(String courierId: courierIds) {
            scooterCourierService.deleteCourierAndReturnResponse(courierId);
        }
        for(String orderId: orderIds) {
            scooterOrdersService.cancelOrderAndReturnResponse(orderId);
        }
    }

    @Step("Create new order and return id")
    private String createNewOrderAndReturnId() {
        String orderTrack = scooterOrdersService.createNewOrderAndReturnTrack();
        return scooterOrdersService.getOrderByTrackAndReturnId(orderTrack);
    }

    @Step("Register courier and return id")
    private String registerCourierAndReturnId() {
        String firstName;
        String login = randomString();
        String password = randomString();
        firstName = randomString();
        String registerRequestBody = "{\"login\":\"" + login + "\","
                + "\"password\":\"" + password + "\","
                + "\"firstName\":\"" + firstName + "\"}";
        scooterCourierService.registerNewCourierAndReturnResponse(registerRequestBody);
        return scooterCourierService.loginAndReturnId(login, password);
    }

    @Step("Verify that order was accepted and status code is 200")
    private void verifyOrderAcceptedAndStatusCode200(String orderId, String courierId) {
        Response response = scooterOrdersService.acceptOrderByOrderIdAndCourierId(orderId, courierId);
        response.then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(200);
    }

    @Step("Verify that response contains \"message\" field with string \"Недостаточно данных для поиска\" and status code is 400")
    private void verifyConflictErrorAndStatusCode400(String orderId, String courierId) {
        Response response = scooterOrdersService.acceptOrderByOrderIdAndCourierId(orderId, courierId);
        response.then().assertThat().body("message", equalTo("Недостаточно данных для поиска"))
                .and()
                .statusCode(400);
    }

    @Step("Verify that response contains \"message\" field with string \"Курьера с таким id не существует\" and status code is 404")
    private void verifyCourierNotFoundAndStatusCode404(String orderId, String courierId) {
        Response response = scooterOrdersService.acceptOrderByOrderIdAndCourierId(orderId, courierId);
        response.then().assertThat().body("message", equalTo("Курьера с таким id не существует"))
                .and()
                .statusCode(404);
    }

    @Step("Verify that response contains \"message\" field with string \"Заказа с таким id не существует\" and status code is 404")
    private void verifyOrderNotFoundAndStatusCode404(String orderId, String courierId) {
        Response response = scooterOrdersService.acceptOrderByOrderIdAndCourierId(orderId, courierId);
        response.then().assertThat().body("message", equalTo("Заказа с таким id не существует"))
                .and()
                .statusCode(404);
    }

    private String createDateString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return formatter.format(date);
    }


}
