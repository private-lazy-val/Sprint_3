package ru.praktikumServices.qaScooter;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;

import org.junit.Ignore;
import org.junit.Test;
import ru.praktikumServices.qaScooter.requests.CancelOrderRequest;
import ru.praktikumServices.qaScooter.requests.CreateOrderRequest;
import ru.praktikumServices.qaScooter.requests.LoginCourierRequest;
import ru.praktikumServices.qaScooter.requests.RegisterCourierRequest;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;

public class ScooterAcceptOrderTest {
    ScooterOrdersService scooterOrdersService;
    ScooterCourierService scooterCourierService;

    List<String> courierIds;
    List<String> orderIds;

    @Before
    public void setUp() {
        scooterOrdersService = new ScooterOrdersService();
        scooterCourierService = new ScooterCourierService();

        courierIds = new ArrayList<>();
        orderIds = new ArrayList<>();
    }

    @After
    public void tearDown() {
        for(String courierId: courierIds) {
            scooterCourierService.deleteCourierAndReturnResponse(courierId);
        }
        for(String orderId: orderIds) {
            CancelOrderRequest cancelOrderRequest = new CancelOrderRequest(orderId);
            scooterOrdersService.cancelOrderAndReturnResponse(cancelOrderRequest);
        }
    }

    //Fails due to incorrect API implementation
    @Ignore
    @DisplayName("Check accept order")
    @Description("Checking if \"ok\" field has flag true in the response and status code is 200")
    @Test
    public void testAcceptOrderReturn200() {
        String orderTrack = scooterOrdersService.createNewOrderAndReturnResponse(CreateOrderRequest.getRandom())
                .assertThat().statusCode(201)
                .extract()
                .body()
                .path("track").toString();

        String orderId = scooterOrdersService.getOrderByTrackAndReturnResponse(orderTrack).assertThat().statusCode(200)
                .extract()
                .body()
                .path("order.id").toString();

        RegisterCourierRequest registerCourierRequest = RegisterCourierRequest.getRandom();
        scooterCourierService.registerNewCourierAndReturnResponse(registerCourierRequest)
                .assertThat()
                .statusCode(201);

        LoginCourierRequest loginCourierRequest = LoginCourierRequest.getLoginCourierRequest(registerCourierRequest);
        String courierId = scooterCourierService.loginCourierWithRequestBodyAndReturnResponse(loginCourierRequest)
                .assertThat().statusCode(SC_OK)
                .extract()
                .body()
                .path("id").toString();

        scooterOrdersService.acceptOrderByOrderIdAndCourierId(orderId, courierId)
                .assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(200);

        courierIds.add(courierId);
        orderIds.add(orderId);
    }

    //Fails due to incorrect API implementation
    @Ignore
    @DisplayName("Check accept order without courier id")
    @Description("Checking if \"message\" field has string \"Недостаточно данных для поиска\" in the response and status code is 400")
    @Test
    public void testAcceptOrderWithoutCourierIdReturn400() {
        String orderTrack = scooterOrdersService.createNewOrderAndReturnResponse(CreateOrderRequest.getRandom())
                .assertThat().statusCode(201)
                .extract()
                .body()
                .path("track").toString();
        String orderId = scooterOrdersService.getOrderByTrackAndReturnResponse(orderTrack).assertThat().statusCode(200)
                .extract()
                .body()
                .path("order.id").toString();
        scooterOrdersService.acceptOrderByOrderIdAndCourierId(orderId, "")
                .assertThat().body("message", equalTo("Недостаточно данных для поиска"))
                .and()
                .statusCode(400);

        orderIds.add(orderId);
    }


    //Fails due to incorrect API implementation
    @Ignore
    @DisplayName("Check accept order without order id")
    @Description("Checking if \"message\" field has string \"Недостаточно данных для поиска\" in the response  and status code is 400")
    @Test
    public void testAcceptOrderWithoutOrderIdReturn400() {
        RegisterCourierRequest registerCourierRequest = RegisterCourierRequest.getRandom();

        scooterCourierService.registerNewCourierAndReturnResponse(registerCourierRequest)
                .assertThat()
                .statusCode(201);
        LoginCourierRequest loginCourierRequest = LoginCourierRequest.getLoginCourierRequest(registerCourierRequest);
        String courierId = scooterCourierService.loginCourierWithRequestBodyAndReturnResponse(loginCourierRequest)
                .assertThat().statusCode(200)
                .extract()
                .body()
                .path("id").toString();
        scooterOrdersService.acceptOrderByOrderIdAndCourierId("", courierId)
                .assertThat().body("message", equalTo("Недостаточно данных для поиска"))
                .and()
                .statusCode(400);

        courierIds.add(courierId);
    }

    //Fails due to incorrect API implementation
    @Ignore
    @DisplayName("Check accept order with wrong courier id")
    @Description("Checking if \"message\" field has string \"Курьера с таким id не существует\" in the response  and status code is 404")
    @Test
    public void testAcceptOrderWithWrongCourierIdReturn404() {
        RegisterCourierRequest registerCourierRequest = RegisterCourierRequest.getRandom();

        scooterCourierService.registerNewCourierAndReturnResponse(registerCourierRequest)
                .assertThat()
                .statusCode(201);

        LoginCourierRequest loginCourierRequest = LoginCourierRequest.getLoginCourierRequest(registerCourierRequest);
        String courierId = scooterCourierService.loginCourierWithRequestBodyAndReturnResponse(loginCourierRequest)
                .assertThat().statusCode(200)
                .extract()
                .body()
                .path("id").toString();

        scooterCourierService.deleteCourierAndReturnResponse(courierId);

        String orderTrack = scooterOrdersService.createNewOrderAndReturnResponse(CreateOrderRequest.getRandom())
                .assertThat().statusCode(201)
                .extract()
                .body()
                .path("track").toString();

        String orderId = scooterOrdersService.getOrderByTrackAndReturnResponse(orderTrack).assertThat().statusCode(200)
                .extract()
                .body()
                .path("order.id").toString();
        scooterOrdersService.acceptOrderByOrderIdAndCourierId(orderId, courierId)
                .assertThat().body("message", equalTo("Курьера с таким id не существует"))
                .and()
                .statusCode(404);
        orderIds.add(orderId);
    }

    // Can't ensure order id doesn't exist by deleting existing order: delete order api method doesn't work
    @Ignore
    @DisplayName("Check accept order with wrong order id")
    @Description("Checking if \"message\" field has string \"Заказа с таким id не существует\" in the response  and status code is 404")
    @Test
    public void testAcceptOrderWithWrongOrderIdReturn404() {
        RegisterCourierRequest registerCourierRequest = RegisterCourierRequest.getRandom();
        scooterCourierService.registerNewCourierAndReturnResponse(registerCourierRequest)
                .assertThat()
                .statusCode(201);

        LoginCourierRequest loginCourierRequest = LoginCourierRequest.getLoginCourierRequest(registerCourierRequest);
        String courierId = scooterCourierService.loginCourierWithRequestBodyAndReturnResponse(loginCourierRequest)
                .assertThat().statusCode(200)
                .extract()
                .body()
                .path("id").toString();

        String orderTrack = scooterOrdersService.createNewOrderAndReturnResponse(CreateOrderRequest.getRandom())
                .assertThat().statusCode(201)
                .extract()
                .body()
                .path("track").toString();

        String orderId = scooterOrdersService.getOrderByTrackAndReturnResponse(orderTrack).assertThat().statusCode(200)
                .extract()
                .body()
                .path("order.id").toString();

        scooterOrdersService.cancelOrderAndReturnResponse(new CancelOrderRequest(orderTrack));

        scooterOrdersService.acceptOrderByOrderIdAndCourierId(orderId, courierId)
                .assertThat().body("message", equalTo("Заказа с таким id не существует"))
                .and()
                .statusCode(404);
        courierIds.add(courierId);
    }
}