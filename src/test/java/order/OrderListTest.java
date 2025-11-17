package order;

import base.BaseTest;
import api.OrderApi;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.notNullValue;

public class OrderListTest extends BaseTest {

    private Integer trackNumber;

    @Before
    @Step("Создание тестового заказа")
    public void setUpTestData() {
        Order order = new Order(
                "Test",
                "User",
                "Test address",
                "1",
                "+7 900 000 00 00",
                1,
                "2024-12-31",
                "Test comment",
                Arrays.asList("BLACK")
        );

        Response response = OrderApi.createOrder(order);
        trackNumber = response.jsonPath().getInt("track");
    }

    @Test
    @DisplayName("В тело ответа возвращается список заказов")
    public void orderListReturnsListOfOrders() {
        Response response = OrderApi.getOrderList();

        response.then()
                .statusCode(SC_OK)
                .body("orders", notNullValue());
    }

    @After
    @Step("Удалить тестовый заказ")
    public void tearDown() {
        try {
            if (trackNumber != null) {
                Response response = OrderApi.cancelOrder(trackNumber);
                if (response.statusCode() != SC_OK) {
                    System.err.println("Не удалось отменить заказ: " + trackNumber);
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка при очистке тестовых данных: " + e.getMessage());
        }
    }
}