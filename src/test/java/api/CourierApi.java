package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.Courier;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;

public class CourierApi {

    @Step("Создание курьера")
    public static Response createCourier(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Авторизация курьера")
    public static Response loginCourier(String login, String password) {
        Courier loginData = new Courier(login, password, null);
        return given()
                .header("Content-type", "application/json")
                .body(loginData)
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Удаление курьера")
    public static Response deleteCourier(String courierId) {
        return given()
                .delete("/api/v1/courier/" + courierId);
    }

    @Step("Получение ID курьера")
    public static String getCourierId(String login, String password) {
        Response loginResponse = loginCourier(login, password);
        if (loginResponse != null && loginResponse.statusCode() == 200) {
            return loginResponse.jsonPath().getString("id");
        }
        return null;
    }

    @Step("Удаление курьера по логину")
    public static void deleteCourierByLogin(String login, String password) {
        if (login != null && password != null) {
            String courierId = getCourierId(login, password);
            if (courierId != null) {
                deleteCourier(courierId);
            }
        }
    }

    @Step("Безопасное удаление курьера")
    public static boolean safeDeleteCourier(String login, String password) {
        try {
            if (login == null || password == null) return false;

            String courierId = getCourierId(login, password);
            if (courierId != null) {
                Response deleteResponse = deleteCourier(courierId);
                return deleteResponse.statusCode() == SC_OK;
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting courier: " + e.getMessage());
            return false;
        }
    }
}