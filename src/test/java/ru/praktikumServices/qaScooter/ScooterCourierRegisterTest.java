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
import static ru.praktikumServices.qaScooter.Utils.randomString;

public class ScooterCourierRegisterTest {

    ScooterCourierService scooterCourierService;
    List<String> ids;

    @Before
    public void setUp() {
        RestAssured.baseURI = Utils.baseURI;
        scooterCourierService = new ScooterCourierService();
        ids = new ArrayList<>();
    }

    @Test
    @DisplayName("Check register courier")
    @Description("Checking if \"ok\" field has flag \"true\" in the response and status code is 201")
    public void testRegisterNewCourierReturn201True() {
        RegisterCourierRequest registerCourierRequest = new RegisterCourierRequest();
        scooterCourierService.registerNewCourierAndReturnResponse(registerCourierRequest)
                .then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(201);

        LoginCourierRequest loginCourierRequest = new LoginCourierRequest(registerCourierRequest.login, registerCourierRequest.password);
        String id = scooterCourierService.loginCourierWithRequestBodyAndReturnResponse(loginCourierRequest)
                .then()
                .assertThat().statusCode(200)
                .extract()
                .body()
                .path("id").toString();

        ids.add(id);
    }


    //Specified response string differs from actual result
    @Test
    @DisplayName("Check register two couriers with same logins")
    @Description("Checking if \"message\" field has string \"Этот логин уже используется\" in the response and status code is 409")
    public void testRegisterTwoCouriersWithSameLoginReturn409UserExists() {
        RegisterCourierRequest registerCourierRequest = new RegisterCourierRequest();
        scooterCourierService.registerNewCourierAndReturnResponse(registerCourierRequest).then()
                .assertThat()
                .statusCode(201);
        scooterCourierService.registerNewCourierAndReturnResponse(registerCourierRequest)
                .then().assertThat().body("message", equalTo("Этот логин уже используется"))
                .and()
                .statusCode(409);

        LoginCourierRequest loginCourierRequest = new LoginCourierRequest(registerCourierRequest.login, registerCourierRequest.password);
        String id = scooterCourierService.loginCourierWithRequestBodyAndReturnResponse(loginCourierRequest)
                .then()
                .assertThat().statusCode(200)
                .extract()
                .body()
                .path("id").toString();
        ids.add(id);
    }


    @Test
    @DisplayName("Check register courier without password")
    @Description("Checking if \"message\" field has string \"Недостаточно данных для создания учетной записи\" in the response and status code is 400")
    public void testRegisterCourierWithoutPasswordReturn400BadRequest() {
        String login = randomString();
        String firstName = randomString();
        RegisterCourierRequest registerCourierRequest = new RegisterCourierRequest(login, null, firstName);
        scooterCourierService.registerNewCourierAndReturnResponse(registerCourierRequest)
                .then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400);
    }


    @Test
    @DisplayName("Check register courier without login")
    @Description("Checking if \"message\" field has string \"Недостаточно данных для создания учетной записи\" in the response and status code is 400")
    public void testRegisterCourierWithoutLoginReturn400BadRequest() {
        String password = randomString();
        String firstName = randomString();
        RegisterCourierRequest registerCourierRequest = new RegisterCourierRequest(null, password, firstName);
        scooterCourierService.registerNewCourierAndReturnResponse(registerCourierRequest)
                .then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400);
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






