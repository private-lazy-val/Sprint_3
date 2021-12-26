package ru.praktikumServices.qaScooter;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;

import org.junit.Test;
import ru.praktikumServices.qaScooter.requests.LoginCourierRequest;
import ru.praktikumServices.qaScooter.requests.RegisterCourierRequest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class ScooterCourierLoginTest {

    ScooterCourierService scooterCourierService;
    List<String> ids;
    String login;
    String password;
    String firstName;

    @Before
    public void setUp() {
        RestAssured.baseURI = Utils.baseURI;
        scooterCourierService = new ScooterCourierService();
        ids = new ArrayList<>();

        RegisterCourierRequest registerCourierRequest = new RegisterCourierRequest();
        scooterCourierService.registerNewCourierAndReturnResponse(registerCourierRequest)
                .then()
                .assertThat()
                .statusCode(201);
        login = registerCourierRequest.login;
        password = registerCourierRequest.password;
        firstName = registerCourierRequest.firstName;

        LoginCourierRequest loginCourierRequest = new LoginCourierRequest(login, password);
        String courierId = scooterCourierService.loginCourierWithRequestBodyAndReturnResponse(loginCourierRequest)
                .then()
                .assertThat().statusCode(200)
                .extract()
                .body()
                .path("id").toString();

        ids.add(courierId);
    }

    @Test
    @DisplayName("Check courier login")
    @Description("Checking if \"id\" field is presented in the response and status code is 200")
    public void testCourierLoginReturn200AndId() {
        LoginCourierRequest loginCourierRequest = new LoginCourierRequest(login, password);
        scooterCourierService.loginCourierWithRequestBodyAndReturnResponse(loginCourierRequest)
                .then().assertThat().body("id", notNullValue())
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Check courier login without login")
    @Description("Checking if \"message\" field has string \"Недостаточно данных для входа\" in the response and status code is 400")
    public void testCourierLoginWithoutLoginReturn404NotFound() {
        LoginCourierRequest loginCourierRequest = new LoginCourierRequest(null, password);
        scooterCourierService.loginCourierWithRequestBodyAndReturnResponse(loginCourierRequest)
                .then().assertThat().body("message", equalTo("Недостаточно данных для входа"))
                .and()
                .statusCode(400);
    }

    //No response from server
    @Test
    @DisplayName("Check courier login without password")
    @Description("Checking if \"message\" field has string \"Недостаточно данных для входа\" in the response and status code is 400")
    public void testCourierLoginWithoutPasswordReturn404NotFound() {
        LoginCourierRequest loginCourierRequest = new LoginCourierRequest(login, null);
        scooterCourierService.loginCourierWithRequestBodyAndReturnResponse(loginCourierRequest)
                .then().assertThat().body("message", equalTo("Недостаточно данных для входа"))
                .and()
                .statusCode(400);
    }

    @Test
    @DisplayName("Check courier login with wrong login")
    @Description("Checking if \"message\" field has string \"Учетная запись не найдена\" in the response and status code is 404")
    public void testCourierLoginWithWrongLoginReturn404NotFound() {
        String wrongLogin = "wrong" + login;
        LoginCourierRequest loginCourierRequest = new LoginCourierRequest(wrongLogin, password);
        scooterCourierService.loginCourierWithRequestBodyAndReturnResponse(loginCourierRequest)
                .then().assertThat().body("message", equalTo("Учетная запись не найдена"))
                .and()
                .statusCode(404);
    }

    @Test
    @DisplayName("Check courier login with wrong password")
    @Description("Checking if \"message\" field has string \"Учетная запись не найдена\" in the response and status code is 404")
    public void testCourierLoginWithWrongPasswordReturn404NotFound() {
        String wrongPassword = "wrong" + password;
        LoginCourierRequest loginCourierRequest = new LoginCourierRequest(login, wrongPassword);
        scooterCourierService.loginCourierWithRequestBodyAndReturnResponse(loginCourierRequest)
                .then().assertThat().body("message", equalTo("Учетная запись не найдена"))
                .and()
                .statusCode(404);
    }

    @After
    public void tearDown() {
        for (String id : ids) {
            if (!id.equals("")) {
                scooterCourierService.deleteCourierAndReturnResponse(id);
            }
        }
    }

}
