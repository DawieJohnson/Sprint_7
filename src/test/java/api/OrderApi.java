package api;

import io.restassured.response.Response;
import models.Order;
import static io.restassured.RestAssured.given;

public class OrderApi {

    public static Response createOrder(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    public static Response cancelOrder(Integer trackNumber) {
        return given()
                .header("Content-type", "application/json")
                .body("{\"track\": " + trackNumber + "}")
                .when()
                .put("/api/v1/orders/cancel");
    }

    public static Response getOrderList() {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get("/api/v1/orders");
    }
}