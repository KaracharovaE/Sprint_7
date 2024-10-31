import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.*;

public class GetOrdersTests {

    private String orderId;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        orderId = createTestOrder();
    }

    @Test
    @DisplayName("Проверка кода /api/v1/orders")
    public void checkOrdersList() {
        checkOrdersResponse();
    }

    @Step("Создание тестового заказа")
    private String createTestOrder() {
        String requestBody = "{ " + "\"firstName\": \"Катя\"," + " " +
                "\"lastName\": \"Петрова\", " +
                "\"address\": \"Мельникайте, 142\", " +
                "\"metroStation\": 4, " +
                "\"phone\": \"+7 800 355 35 35\", " +
                "\"rentTime\": 5, " +
                "\"deliveryDate\": \"2024-11-01\", " +
                "\"comment\": \"Быстрее\", " +
                "\"color\": [\"BLACK\"] " + "}";

        return given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/v1/orders")
                .then()
                .statusCode(201)
                .extract().path("id");
    }

    @Step("Проверка ответа на запрос списка заказов")
    private void checkOrdersResponse() {
        given()
                .when()
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .body("orders", is(notNullValue()))
                .body("orders", is(instanceOf(java.util.List.class)))
                .body("orders.size()", greaterThan(0));
    }

    @Step("Удаление тестового заказа")
    private void deleteTestOrder(String orderId) {
        given()
                .pathParam("id", orderId)
                .when()
                .delete("/api/v1/orders/{id}")
                .then()
                .statusCode(200);
    }

    @After
    public void tearDown() {
        if (orderId != null) {
            deleteTestOrder(orderId);
        }
    }
}