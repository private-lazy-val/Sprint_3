package ru.praktikumServices.qaScooter.requests;

import java.util.Random;

import static ru.praktikumServices.qaScooter.Utils.createDateString;
import static ru.praktikumServices.qaScooter.Utils.randomString;

public class CreateOrderRequest extends Request {
    private static Random random = new Random();

    public String firstName = randomString();
    public String lastName = randomString();
    public String address = randomString();
    public String metroStation = randomString();
    public String phone = randomString();
    public int rentTime = random.nextInt();
    public String deliveryDate = createDateString();
    public String comment = randomString();
    public String[] colors;

    public CreateOrderRequest() {

    }

    public CreateOrderRequest(String[] colors) {
        this.colors = colors;
    }
}