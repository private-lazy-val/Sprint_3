package ru.praktikumServices.qaScooter.requests;

public class CancelOrderRequest extends Request {
    public String track;

    public CancelOrderRequest(String track) {
        this.track = track;
    }
}