package base;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.junit.Before;

public class BaseTest {

    @Before
    @Step("Настройка базового URL")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }
}