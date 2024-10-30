import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import sprint7.CourierClient;
import sprint7.Courier;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static sprint7.CourierGenerator.randomCourier;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.junit.Assert.assertEquals;

public class CreateCourierTests {

    private CourierClient courierClient;
    private int id;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        courierClient = new CourierClient();
    }

    @Test
    @DisplayName("Создание курьера")
    public void createCourier() {
        Courier courier = createRandomCourier();
        Response response = createCourierAndAssert(courier, SC_CREATED);
        verifyResponseContainsTrue(response);
    }

    @Step("Создаем случайного курьера")
    private Courier createRandomCourier() {
        return randomCourier();
    }

    @Step("Создаем курьера и проверяем статус")
    private Response createCourierAndAssert(Courier courier, int expectedStatusCode) {
        Response response = courierClient.create(courier);
        assertEquals("Неверный статус код", expectedStatusCode, response.statusCode());
        return response;
    }

    @Step("Проверяем, что ответ содержит 'true'")
    private void verifyResponseContainsTrue(Response response) {
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains("true"));
    }

    @Test
    @DisplayName("Создание двух идентичных курьеров")
    public void createTwoIdenticalCouriers() {
        Courier courier = randomCourier();

        Response firstResponse = createCourierAndAssert(courier, SC_CREATED);
        Response secondResponse = createCourierAndAssert(courier, SC_CONFLICT);
        verifyConflictResponse(secondResponse);
    }

    @Step("Проверяем сообщение об ошибке")
    private void verifyConflictResponse(Response response) {
        assertEquals("Неверный статус код", SC_CONFLICT, response.statusCode());
        assertTrue(response.getBody().asString().contains("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Создание курьера без логина")
    public void createCourierWithoutLogin() {
        Courier courier = createRandomCourierWithMissingField(null, "password");
        Response response = attemptToCreateCourier(courier);
        verifyBadRequestResponse(response, "Недостаточно данных для создания учетной записи");
    }

    @Step("Создаем случайного курьера без логина или пароля")
    private Courier createRandomCourierWithMissingField(String login, String password) {
        Courier courier = randomCourier();
        if (login == null) {
            courier.setLogin(null);
        }
        if (password == null) {
            courier.setPassword(null);
        }
        return courier;
    }

    @Step("Пытаемся создать курьера")
    private Response attemptToCreateCourier(Courier courier) {
        return courierClient.create(courier);
    }

    @Step("Проверяем сообщение об ошибке")
    private void verifyBadRequestResponse(Response response, String expectedMessage) {
        assertEquals("Неверный статус код", SC_BAD_REQUEST, response.statusCode());
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains(expectedMessage));
    }

    @Test
    @DisplayName("Создание курьера без пароля")
    public void createCourierWithoutPassword() {
        Courier courier = createRandomCourierWithMissingField("login", null);
        Response response = attemptToCreateCourier(courier);
        verifyBadRequestResponse(response, "Недостаточно данных для создания учетной записи");
    }

    @After
    public void tearDown() {
        if (id != 0) {
            courierClient.delete(id);
        }
    }
}



