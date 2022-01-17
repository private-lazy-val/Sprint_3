package ru.praktikumServices.qaScooter;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.praktikumServices.qaScooter.requests.CancelOrderRequest;
import ru.praktikumServices.qaScooter.requests.CreateOrderRequest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class ScooterCreateOrderTest {

    ScooterOrdersService scooterOrdersService;
    AllureLifecycle oLifecycle = Allure.getLifecycle();
    String[] colors;
    List<String> tracks;

    public ScooterCreateOrderTest(String[] colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters
    public static Object[][] getOrderColorData() {
        return new Object[][]{
                {new String[]{"BLACK"}},
                {new String[]{"GREY"}},
                {new String[]{"BLACK", "GREY"}},
                {null},
        };
    }

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
    @Description("Checking if \"track\" field is presented in the response and status code is 201")
    public void testCreateNewOrderReturn201Ok() {
        String colorsString = Utils.serializeToJsonIgnoreNulls(this.colors);
        oLifecycle.updateTestCase(testResult -> testResult.setName("Check create new order with color field = \"" + colorsString + "\""));

        CreateOrderRequest createOrderRequest = CreateOrderRequest.getRandom(colors);
        String track = scooterOrdersService.createNewOrderAndReturnResponse(createOrderRequest)
                .assertThat().body("track", notNullValue())
                .and()
                .statusCode(201)
                .extract().body().path("track").toString();

        tracks.add(track);
    }


}
