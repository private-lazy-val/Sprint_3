package ru.praktikumServices.qaScooter.requests;

import static ru.praktikumServices.qaScooter.Utils.randomString;

public class RegisterCourierRequest extends Request {
    public String login;
    public String password;
    public String firstName;

    public RegisterCourierRequest(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }

    public RegisterCourierRequest() {
        firstName = randomString();
        login = randomString();
        password = randomString();
    }
}