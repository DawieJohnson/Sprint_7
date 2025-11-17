package courier;

import base.BaseTest;
import api.CourierApi;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.Courier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class CourierLoginTest extends BaseTest {

    private Courier courier;

    @Before
    @Step("Создание тестового курьера")
    public void setUpTestData() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        courier = new Courier("login_courier_" + timestamp, "1234", "saske");

        CourierApi.createCourier(courier)
                .then()
                .statusCode(SC_CREATED);
    }

    @Test
    @DisplayName("Курьер может авторизоваться")
    public void courierCanLogin() {
        Response response = CourierApi.loginCourier(courier.getLogin(), courier.getPassword());

        response.then()
                .statusCode(SC_OK)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Нельзя авторизоваться без логина")
    public void cannotLoginWithoutLogin() {
        Response response = CourierApi.loginCourier(null, courier.getPassword());

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Нельзя авторизоваться без пароля")
    public void cannotLoginWithoutPassword() {
        Response response = CourierApi.loginCourier(courier.getLogin(), null);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка при неправильном логине")
    public void errorWithWrongLogin() {
        Response response = CourierApi.loginCourier("wrong_login", courier.getPassword());

        response.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка при неправильном пароле")
    public void errorWithWrongPassword() {
        Response response = CourierApi.loginCourier(courier.getLogin(), "wrong_password");

        response.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка при авторизации под несуществующим пользователем")
    public void errorWithNonExistentUser() {
        Response response = CourierApi.loginCourier("non_existent_user", "any_password");

        response.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @After
    @Step("Удаление тестового курьера")
    public void tearDown() {
        try {
            if (courier != null && courier.getLogin() != null) {
                CourierApi.safeDeleteCourier(courier.getLogin(), courier.getPassword());
            }
        } catch (Exception e) {
            System.err.println("Ошибка при очистке тестовых данных: " + e.getMessage());
        }
    }
}