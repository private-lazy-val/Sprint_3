package ru.praktikumServices.qaScooter.requests;

public class LoginCourierRequest extends Request {
    public String login;
    public String password;

    public LoginCourierRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }
}