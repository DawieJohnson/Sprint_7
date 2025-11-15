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
import static org.hamcrest.Matchers.equalTo;

public class CourierCreationTest extends BaseTest {

    private Courier courier;

    @Before
    @Step("Создание тестовых данных")
    public void setUpTestData() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        courier = new Courier("courier_" + timestamp, "1234", "saske");
    }

    @Test
    @DisplayName("Курьера можно создать")
    public void courierCanBeCreated() {
        Response response = CourierApi.createCourier(courier);

        response.then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void cannotCreateDuplicateCourier() {
        CourierApi.createCourier(courier);

        Response response = CourierApi.createCourier(courier);

        response.then()
                .statusCode(SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Нельзя создать курьера без логина")
    public void cannotCreateCourierWithoutLogin() {
        Courier courierWithoutLogin = new Courier(null, "1234", "saske");

        Response response = CourierApi.createCourier(courierWithoutLogin);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Нельзя создать курьера без пароля")
    public void cannotCreateCourierWithoutPassword() {
        Courier courierWithoutPassword = new Courier("ninja", null, "saske");

        Response response = CourierApi.createCourier(courierWithoutPassword);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Нельзя создать курьера без имени")
    public void cannotCreateCourierWithoutFirstName() {
        Courier courierWithoutFirstName = new Courier("ninja", "1234", null);

        Response response = CourierApi.createCourier(courierWithoutFirstName);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @After
    @Step("Удалить тестового курьера")
    public void tearDown() {
        if (courier != null && courier.getLogin() != null) {
            CourierApi.deleteCourierByLogin(courier.getLogin(), courier.getPassword());
        }
    }
}