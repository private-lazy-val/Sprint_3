package ru.praktikumServices.qaScooter;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import ru.praktikumServices.qaScooter.requests.LoginCourierRequest;
import ru.praktikumServices.qaScooter.requests.RegisterCourierRequest;

import static org.hamcrest.Matchers.equalTo;

public class ScooterCourierDeleteTest {

    ScooterCourierService scooterCourierService;

    @Before
    @Step("Registering new courier")
    public void setUp() {
        scooterCourierService = new ScooterCourierService();
    }

    @DisplayName("Check delete courier")
    @Description("Checking if \"ok\" field has flag true in the response and status code is 200")
    @Test
    public void testDeleteCourierReturn200Ok() {
        RegisterCourierRequest registerCourierRequest = RegisterCourierRequest.getRandom();
        scooterCourierService.registerNewCourierAndReturnResponse(registerCourierRequest)
                .assertThat()
                .statusCode(201);
        LoginCourierRequest loginCourierRequest = LoginCourierRequest.getLoginCourierRequest(registerCourierRequest);
        String id = scooterCourierService.loginCourierWithRequestBodyAndReturnResponse(loginCourierRequest).
                assertThat().statusCode(200).
                extract().body().path("id").toString();
        ValidatableResponse response = scooterCourierService.deleteCourierAndReturnResponse(id);
        response.assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(200);
    }

    // Specified response string and status code differ from actual result
    @DisplayName("Delete courier without id")
    @Description("Checking if \"message\" field has string \"Недостаточно данных для создания учетной записи\" and status code is 400")
    @Test
    public void testDeleteCourierWithoutIdReturn400BadRequest() {
        scooterCourierService.deleteCourierAndReturnResponse("")
                .assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400);
    }


    // Specified response string differs from actual result
    @DisplayName("Delete courier with wrong id")
    @Description("Checking if \"message\" field has string \"Курьера с таким id нет\" and status code is 404")
    @Test
    public void testDeleteCourierWithWrongIdReturn404NotFound() {
        RegisterCourierRequest registerCourierRequest = RegisterCourierRequest.getRandom();
        scooterCourierService.registerNewCourierAndReturnResponse(registerCourierRequest)
                .assertThat()
                .statusCode(201);
        LoginCourierRequest loginCourierRequest = LoginCourierRequest.getLoginCourierRequest(registerCourierRequest);
        String id = scooterCourierService.loginCourierWithRequestBodyAndReturnResponse(loginCourierRequest)
                .assertThat().statusCode(200)
                .extract().body().path("id").toString();
        scooterCourierService.deleteCourierAndReturnResponse(id)
                .assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(200);
        scooterCourierService.deleteCourierAndReturnResponse(id)
                .assertThat().body("message", equalTo("Курьера с таким id нет"))
                .and()
                .statusCode(404);
    }
}
