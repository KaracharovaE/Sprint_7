import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import sprint7.OrderRequest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class MakeOrderTests {

    private final String firstName = "Лена";
    private final String lastName = "Иванова";
    private final String address = "Ушакова, 142";
    private final String metroStation = "4";
    private final String phone = "+7 800 355 35 36";
    private final int rentTime = 5;
    private final String deliveryDate = "2024-11-06";
    private final String comment = "Жду";
    private final String color;
    private final boolean shouldContainTrack;
    private Integer track;

    public MakeOrderTests(String color, boolean shouldContainTrack) {
        this.color = color;
        this.shouldContainTrack = shouldContainTrack;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Parameterized.Parameters
    public static Object[][] data() {
        return new Object[][] {
                { "BLACK", true },
                { "GREY", true },
                { "BLACK, GREY", true },
                { null, true }
        };
    }

    @Test
    @DisplayName("Проверка статус кода /api/v1/orders")
    public void testCreateOrder() {
        Response response = given()
                .header("Content-Type", "application/json")
                .body(createOrderRequestBody(color))
                .when()
                .post("/api/v1/orders")
                .then()
                .statusCode(201)
                .extract()
                .response();

        if (shouldContainTrack) {
            response.then().body("track", notNullValue());
        }
    }

    @Step("Удалить созданный заказ")
    public void deleteOrderTest() {
        if (track != null) {
            given()
                    .header("Content-Type", "application/json")
                    .pathParam("track", track)
                    .when()
                    .delete("/api/v1/orders/{track}")
                    .then()
                    .statusCode(200);
        }
    }

    @Step("Создаем тело запроса для заказа с цветом")
    private String createOrderRequestBody(String color) {
        return new Gson().toJson(new OrderRequest(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color));
    }

    @After
    public void tearDown() {
        deleteOrderTest();
    }
}