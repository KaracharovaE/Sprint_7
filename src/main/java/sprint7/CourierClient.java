package sprint7;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CourierClient {

    public Response create(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("api/v1/courier");
    }

    public Response login(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(CourierCreds.credsFromCourier(courier))
                .when()
                .post("api/v1/courier/login");
    }

    public Response delete(int id) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .when()
                .delete("api/v1/courier/" + id);
    }
}