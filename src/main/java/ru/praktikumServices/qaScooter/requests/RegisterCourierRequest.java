package ru.praktikumServices.qaScooter.requests;

import org.apache.commons.lang3.RandomStringUtils;

public class RegisterCourierRequest extends Request {
    public final String login;
    public final String password;
    public final String firstName;

    public RegisterCourierRequest(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }

    public static RegisterCourierRequest getRandom() {
        final String firstName = RandomStringUtils.randomAlphabetic(10);
        final String login = RandomStringUtils.randomAlphabetic(10);
        final String password = RandomStringUtils.randomAlphabetic(10);
        return new RegisterCourierRequest(login, password, firstName);
    }
}