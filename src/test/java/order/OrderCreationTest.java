package order;

import base.BaseTest;
import api.OrderApi;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.Order;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreationTest extends BaseTest {

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
        Order order = createOrderWithColor(color);
        Response response = OrderApi.createOrder(order);

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

    @Step("Проверить успешное создание заказа")
    private void verifyOrderCreatedSuccessfully(Response response) {
        response.then()
                .statusCode(SC_CREATED)
                .body("track", notNullValue());
    }

    @After
    @Step("Отменить тестовый заказ")
    public void tearDown() {
        if (trackNumber != null) {
            OrderApi.cancelOrder(trackNumber)
                    .then()
                    .statusCode(SC_OK);
        }
    }
}