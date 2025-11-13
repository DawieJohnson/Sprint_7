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
import static org.hamcrest.Matchers.equalTo;

public class CourierCreationTest {

    private Courier courier;

    @Before
    @Step("Создание тестовых данных")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        String timestamp = String.valueOf(System.currentTimeMillis());
        courier = new Courier("courier_" + timestamp, "1234", "saske");
    }

    @Test
    @DisplayName("Курьера можно создать")
    public void courierCanBeCreated() {
        Response response = createCourier(courier);

        response.then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void cannotCreateDuplicateCourier() {
        createCourier(courier);

        Response response = createCourier(courier);

        response.then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Нельзя создать курьера без логина")
    public void cannotCreateCourierWithoutLogin() {
        Courier courierWithoutLogin = new Courier(null, "1234", "saske");

        Response response = createCourier(courierWithoutLogin);

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Нельзя создать курьера без пароля")
    public void cannotCreateCourierWithoutPassword() {
        Courier courierWithoutPassword = new Courier("ninja", null, "saske");

        Response response = createCourier(courierWithoutPassword);

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Нельзя создать курьера без имени")
    public void cannotCreateCourierWithoutFirstName() {
        Courier courierWithoutFirstName = new Courier("ninja", "1234", null);

        Response response = createCourier(courierWithoutFirstName);

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Step("Создать курьера")
    private Response createCourier(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @After
    @Step("Удалить тестового курьера")
    public void tearDown() {
        if (courier != null && courier.getLogin() != null) {
            Response loginResponse = given()
                    .header("Content-type", "application/json")
                    .body(new Courier(courier.getLogin(), courier.getPassword(), null))
                    .when()
                    .post("/api/v1/courier/login");

            if (loginResponse.statusCode() == 200) {
                String courierId = loginResponse.jsonPath().getString("id");
                given()
                        .delete("/api/v1/courier/" + courierId)
                        .then()
                        .statusCode(200);
            }
        }
    }
}