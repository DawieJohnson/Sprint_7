package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.Order;
import static io.restassured.RestAssured.given;

public class OrderApi {

    @Step("Создание заказа")
    public static Response createOrder(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    @Step("Отмена заказа с track номером: {trackNumber}")
    public static Response cancelOrder(Integer trackNumber) {
        return given()
                .header("Content-type", "application/json")
                .param("track", trackNumber)
                .when()
                .put("/api/v1/orders/cancel");
    }

    @Step("Получение списка заказов")
    public static Response getOrderList() {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get("/api/v1/orders");
    }
}