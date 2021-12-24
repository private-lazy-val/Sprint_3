package ru.praktikumServices.qaScooter;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

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
                {new String[]{}},
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
        scooterOrdersService = new ScooterOrdersService();
        tracks = new ArrayList<>();
    }

    @Test
    @Description("Checking if \"track\" field is presented in the response and status code is 201")
    public void testCreateNewOrderReturn201Ok() {
        String colorField = createColorField();
        oLifecycle.updateTestCase(testResult -> testResult.setName("Check create new order with color field = \"" + colorField + "\""));
        String newOrderBody = scooterOrdersService.createNewOrderRequestBody(colorField);
        Response response = scooterOrdersService.createNewOrderAndReturnResponse(newOrderBody);
        verifyTrackIsNotNullAndStatusCode201(response);
        verifyOrderCreatedAndAddTrackToListForDeletion(response);
    }



    @Step("Verify that response contains track which is not null and status code is 201")
    private void verifyTrackIsNotNullAndStatusCode201(Response response) {
        response.then().assertThat().body("track", notNullValue())
                .and()
                .statusCode(201);
    }

    @Step("Verify that new order was created and add its track to list for further deletion after the test is completed")
    private void verifyOrderCreatedAndAddTrackToListForDeletion(Response response) {
        if (response.statusCode() == 201) {
            String track = response.body().path("track").toString();
            tracks.add(track);
        }
    }

    @After
    public void tearDown() {
        for (String track : tracks) {
            scooterOrdersService.cancelOrderAndReturnResponse(track);
        }
    }

    private String createColorField() {
        String colorField = "";
        if (colors.length != 0) {
            StringJoiner colorJoiner = new StringJoiner(",", "[", "]");
            for (int i = 0; i < colors.length; i++) {
                colorJoiner.add("\"" + colors[i] + "\"");
            }
            colorField = colorJoiner.toString();
        }
        return colorField;
    }

}
