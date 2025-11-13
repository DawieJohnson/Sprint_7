package courier;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.Courier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CourierLoginTest {

    private Courier courier;
    private String courierId;

    @Before
    @Step("Создание тестового курьера")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";

        String timestamp = String.valueOf(System.currentTimeMillis());
        courier = new Courier("login_courier_" + timestamp, "1234", "saske");

        given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("Курьер может авторизоваться")
    public void courierCanLogin() {
        Response response = loginCourier(courier.getLogin(), courier.getPassword());

        response.then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Нельзя авторизоваться без логина")
    public void cannotLoginWithoutLogin() {
        Response response = loginCourier(null, courier.getPassword());

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Нельзя авторизоваться без пароля")
    public void cannotLoginWithoutPassword() {
        Response response = loginCourier(courier.getLogin(), null);

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка при неправильном логине")
    public void errorWithWrongLogin() {
        Response response = loginCourier("wrong_login", courier.getPassword());

        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка при неправильном пароле")
    public void errorWithWrongPassword() {
        Response response = loginCourier(courier.getLogin(), "wrong_password");

        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка при авторизации под несуществующим пользователем")
    public void errorWithNonExistentUser() {
        Response response = loginCourier("non_existent_user", "any_password");

        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Step("Авторизация курьера")
    private Response loginCourier(String login, String password) {
        Courier loginData = new Courier(login, password, null);

        return given()
                .header("Content-type", "application/json")
                .body(loginData)
                .when()
                .post("/api/v1/courier/login");
    }

    @After
    @Step("Удаление тестового курьера")
    public void tearDown() {
        if (courier != null && courier.getLogin() != null) {
            Response loginResponse = given()
                    .header("Content-type", "application/json")
                    .body(new Courier(courier.getLogin(), courier.getPassword(), null))
                    .when()
                    .post("/api/v1/courier/login");

            if (loginResponse.statusCode() == 200) {
                courierId = loginResponse.jsonPath().getString("id");
                given()
                        .delete("/api/v1/courier/" + courierId)
                        .then()
                        .statusCode(200);
            }
        }
    }
}