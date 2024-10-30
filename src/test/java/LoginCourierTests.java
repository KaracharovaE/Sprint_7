import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sprint7.Courier;
import sprint7.CourierClient;
import sprint7.CourierId;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static sprint7.CourierGenerator.randomCourier;

public class LoginCourierTests {

    private CourierClient courierClient;
    private int id;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        courierClient = new CourierClient();
    }

    @Test
    @DisplayName("Курьер может авторизоваться")
    public void shouldAuthorizeCourierWithValidCredentials() {
        Courier courier = createRandomCourier();
        createCourier(courier);
        Response loginResponse = loginCourier(courier);
        verifySuccessfulLogin(loginResponse);
    }

    @Step("Создаем случайного курьера")
    private Courier createRandomCourier() {
        return randomCourier();
    }

    @Step("Создаем курьера")
    private void createCourier(Courier courier) {
        Response response = courierClient.create(courier);
        assertEquals("Неверный статус код при создании курьера", SC_CREATED, response.statusCode());
    }

    @Step("Авторизуем курьера")
    private Response loginCourier(Courier courier) {
        Response loginResponse = courierClient.login(courier);
        id = loginResponse.as(CourierId.class).getId();
        return loginResponse;
    }

    @Step("Проверяем успешную авторизацию")
    private void verifySuccessfulLogin(Response loginResponse) {
        assertEquals("Неверный статус код", SC_OK, loginResponse.statusCode());
    }


    @Test
    @DisplayName("Курьер не может авторизоваться без пароля")
    public void shouldNotAuthorizeCourierWithoutPassword() {
        Courier courier = createRandomCourierWithoutPassword();
        Response loginResponse = attemptToLoginWithoutPassword(courier);
        verifyBadRequestResponseWithoutPassword(loginResponse);
    }

    @Step("Создаем случайного курьера без пароля")
    private Courier createRandomCourierWithoutPassword() {
        Courier courier = randomCourier();
        courier.withEmptyPassword();
        return courier;
    }

    @Step("Пытаемся авторизоваться курьером без пароля")
    private Response attemptToLoginWithoutPassword(Courier courier) {
        return courierClient.login(courier);
    }

    @Step("Проверяем, что ответ содержит ошибку о недостаточных данных для входа")
    private void verifyBadRequestResponseWithoutPassword(Response loginResponse) {
        assertEquals("Неверный статус код", SC_BAD_REQUEST, loginResponse.statusCode());
        assertTrue(loginResponse.getBody().asString().contains("Недостаточно данных для входа"));
    }


    @Test
    @DisplayName("Курьер не может авторизоваться без логина")
    public void shouldNotAuthorizeCourierWithoutLogin() {
        Courier courier = createRandomCourierWithoutLogin();
        Response loginResponse = attemptToLoginWithoutLogin(courier);
        verifyBadRequestResponseWithoutLogin(loginResponse);
    }

    @Step("Создаем случайного курьера без логина")
    private Courier createRandomCourierWithoutLogin() {
        Courier courier = randomCourier();
        courier.setLogin(null); // Устанавливаем логин в null
        return courier;
    }

    @Step("Пытаемся авторизоваться курьером без логина")
    private Response attemptToLoginWithoutLogin(Courier courier) {
        return courierClient.login(courier);
    }

    @Step("Проверяем, что ответ содержит ошибку о недостаточных данных для входа")
    private void verifyBadRequestResponseWithoutLogin(Response loginResponse) {
        assertEquals("Неверный статус код", SC_BAD_REQUEST, loginResponse.statusCode());
        assertTrue(loginResponse.getBody().asString().contains("Недостаточно данных для входа"));
    }



    @Test
    @DisplayName("Система вернёт ошибку, если неправильно указать логин")
    public void shouldReturnErrorForWrongLogin() {
        Courier courier = createAndRegisterCourier();
        Courier wrongPasswordCourier = createCourierWithWrongLogin(courier);
        Response loginResponse = attemptToLoginWithWrongLogin(wrongPasswordCourier);
        verifyErrorResponseForWrongLogin(loginResponse);
    }

    @Step("Создаем и регистрируем курьера")
    private Courier createAndRegisterCourier() {
        Courier courier = randomCourier();
        courierClient.create(courier);
        return courier;
    }

    @Step("Создаем курьера с неправильным логином")
    private Courier createCourierWithWrongLogin(Courier courier) {
        return new Courier()
                .withLogin("wrongLogin")
                .withPassword(courier.getLogin());
    }

    @Step("Пытаемся авторизоваться с неправильным логином")
    private Response attemptToLoginWithWrongLogin(Courier courier) {
        return courierClient.login(courier);
    }

    @Step("Проверяем, что ответ содержит ошибку для неправильного логина")
    private void verifyErrorResponseForWrongLogin(Response loginResponse) {
        assertEquals("Неверный статус код", SC_NOT_FOUND, loginResponse.statusCode());
        assertTrue(loginResponse.getBody().asString().contains("Учетная запись не найдена"));
    }


    @Test
    @DisplayName("Система вернёт ошибку, если неправильно указать пароль")
    public void shouldReturnErrorForWrongPassword() {
        Courier courier = createAndRegisterCourierForWrongPassword();
        Courier wrongPasswordCourier = createCourierWithWrongPassword(courier);
        Response loginResponse = attemptToLoginWrongPassword(wrongPasswordCourier);
        verifyErrorResponseForWrongPassword(loginResponse);
    }

    @Step("Создаем и регистрируем курьера")
    private Courier createAndRegisterCourierForWrongPassword() {
        Courier courier = randomCourier();
        courierClient.create(courier);
        return courier;
    }

    @Step("Создаем курьера с неправильным паролем")
    private Courier createCourierWithWrongPassword(Courier courier) {
        return new Courier()
                .withLogin(courier.getLogin())
                .withPassword("wrongPassword");
    }

    @Step("Пытаемся авторизоваться с неправильным паролем")
    private Response attemptToLoginWrongPassword(Courier courier) {
        return courierClient.login(courier);
    }

    @Step("Проверяем, что ответ содержит ошибку для неправильного пароля")
    private void verifyErrorResponseForWrongPassword(Response loginResponse) {
        assertEquals("Неверный статус код", SC_NOT_FOUND, loginResponse.statusCode());
        assertTrue(loginResponse.getBody().asString().contains("Учетная запись не найдена"));
    }




    @Test
    @DisplayName("Система вернёт ошибку, если авторизоваться под несуществующим пользователем")
    public void shouldNotAuthorizeNonExistentCourier() {
        Courier nonExistentCourier = createNonExistentCourier();
        Response loginResponse = attemptToLoginNonExistentCourier(nonExistentCourier);
        verifyErrorResponseForNonExistentCourier(loginResponse);
    }

    @Step("Создаем курьера с несуществующими данными")
    private Courier createNonExistentCourier() {
        return new Courier()
                .withLogin("nonExistentLogin")
                .withPassword("nonExistentPassword");
    }

    @Step("Пытаемся авторизоваться под несуществующим курьером")
    private Response attemptToLoginNonExistentCourier(Courier courier) {
        return courierClient.login(courier);
    }

    @Step("Проверяем, что ответ содержит ошибку для несуществующего курьера")
    private void verifyErrorResponseForNonExistentCourier(Response loginResponse) {
        assertEquals("Неверный статус код", SC_NOT_FOUND, loginResponse.statusCode());
        assertTrue(loginResponse.getBody().asString().contains("Учетная запись не найдена"));
    }


    @After
    public void tearDown() {
        if (id != 0) {
            courierClient.delete(id);
        }
    }
}
