package order;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class OrderListTest {

    private Integer trackNumber;

    @Before
    @Step("Настройка базового URI и создание тестового заказа")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";

        Order order = new Order(
                "Test",
                "User",
                "Test address",
                "1",
                "+7 900 000 00 00",
                1,
                "2024-01-01",
                "Test comment",
                Arrays.asList("BLACK")
        );

        Response response = given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post("/api/v1/orders");

        trackNumber = response.jsonPath().getInt("track");
    }

    @Test
    @DisplayName("В тело ответа возвращается список заказов")
    public void orderListReturnsListOfOrders() {
        Response response = getOrderList();

        verifyOrderListResponse(response);
    }

    @Step("Получить список заказов")
    private Response getOrderList() {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get("/api/v1/orders");
    }

    @Step("Проверить что возвращается список заказов")
    private void verifyOrderListResponse(Response response) {
        response.then()
                .statusCode(200)
                .body("orders", notNullValue());
    }

    @After
    @Step("Удалить тестовый заказ")
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