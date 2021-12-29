package ru.praktikumServices.qaScooter.requests;

public class LoginCourierRequest extends Request {
    public final String login;
    public final String password;

    public LoginCourierRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public static LoginCourierRequest getLoginCourierRequest(RegisterCourierRequest registeredCourier) {
        return new LoginCourierRequest(registeredCourier.login, registeredCourier.password);
    }
}