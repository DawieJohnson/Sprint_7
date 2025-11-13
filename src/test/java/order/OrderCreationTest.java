package order;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.Order;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreationTest {

    private final List<String> color;
    private Integer trackNumber;

    public OrderCreationTest(List<String> color) {
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][] getColorData() {
        return new Object[][] {
                {Arrays.asList("BLACK")},
                {Arrays.asList("GREY")},
                {Arrays.asList("BLACK", "GREY")},
                {null}
        };
    }

    @Test
    @DisplayName("Создание заказа с разными вариантами цвета")
    public void orderCanBeCreatedWithDifferentColors() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";

        Order order = createOrderWithColor(color);
        Response response = createOrder(order);

        trackNumber = response.jsonPath().getInt("track");
        verifyOrderCreatedSuccessfully(response);
    }

    @Step("Создать тестовый заказ")
    private Order createOrderWithColor(List<String> color) {
        return new Order(
                "Naruto",
                "Uchiha",
                "Konoha, 142 apt.",
                "4",
                "+7 800 355 35 35",
                5,
                "2020-06-06",
                "Saske, come back to Konoha",
                color
        );
    }

    @Step("Создать заказ через API")
    private Response createOrder(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    @Step("Проверить успешное создание заказа")
    private void verifyOrderCreatedSuccessfully(Response response) {
        response.then()
                .statusCode(201)
                .body("track", notNullValue());
    }

    @After
    @Step("Отменить тестовый заказ")
    public void tearDown() {
        if (trackNumber != null) {
            given()
                    .header("Content-type", "application/json")
                    .body("{\"track\": " + trackNumber + "}")
                    .when()
                    .put("/api/v1/orders/cancel")
                    .then()
                    .statusCode(200);
        }
    }
}