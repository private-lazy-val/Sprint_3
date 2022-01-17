package ru.praktikumServices.qaScooter.requests;

public class CancelOrderRequest extends Request {
    public final String track;

    public CancelOrderRequest(String track) {
        this.track = track;
    }
}