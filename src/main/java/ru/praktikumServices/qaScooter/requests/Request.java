package ru.praktikumServices.qaScooter.requests;

import ru.praktikumServices.qaScooter.Utils;

public class Request {
    public String toJsonString() {
        return Utils.serializeToJsonIgnoreNulls(this);
    }
}
