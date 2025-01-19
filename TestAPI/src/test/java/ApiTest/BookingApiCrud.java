package ApiTest;

import groovy.lang.GString;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class BookingApiCrud {
private static String authToken;
private static int bookingId;

@BeforeClass
    public void setup () {
        RestAssured.baseURI="https://restful-booker.herokuapp.com";
        authToken =AuthHelper.getAuthToken();// fetching token for authentication
    }
//Creating a booking
@Test(priority = 1)
public void testCreateBooking() {

        String requestBody = "{ \"firstname\": \"John\", \"lastname\": \"Doe\", \"totalprice\": 150, \"depositpaid\": true, " +
                "\"bookingdates\": {\"checkin\": \"2024-01-01\", \"checkout\": \"2024-01-05\"}, \"additionalneeds\": \"Breakfast\" }";


    Response response =given().
            contentType(ContentType.JSON)
            .body(requestBody)
            .log().all()
            .when()
            .post("/booking")
            .then()
            .log().all()
            .statusCode(200)
            .body("booking.firstname",equalTo("John"))
            .body("booking.lastname",equalTo("Doe"))
            .extract().response();

    bookingId =response.jsonPath().getInt("bookingid");
    }

@Test(priority = 2,description = "updating existing booking")
public void testUpdateBooking() {


    String updateRequestBody = "{ \"firstname\": \"Jane\", \"lastname\": \"Smith\", \"totalprice\": 200, \"depositpaid\": false, " +
            "\"bookingdates\": {\"checkin\": \"2024-02-01\", \"checkout\": \"2024-02-15\"}, \"additionalneeds\": \"Lunch\" }";
    Response response =given().

            contentType(ContentType.JSON)
            .header("Cookie","token=" +authToken)
            .body(updateRequestBody)
            .pathParams("id",bookingId)
            .log().all()
            .when()
            .post("/booking/{id}")
            .then()
            .log().all()
            .statusCode(200)
            .body("booking.firstname",equalTo("Jane"))
            .body("booking.lastname",equalTo("Smith")) // validating wrong name
            .extract().response();

    System.out.println("Updated Resposne: " + response.prettyPrint());

}
@Test(priority = 3,description = "negative test case")
public void testUpdateBookingWithoutId()
{
    String updatedRequestBody = "{ \"firstname\": \"Jane\", \"lastname\": \"Smith\", \"totalprice\": 200, \"depositpaid\": false, " +
            "\"bookingdates\": {\"checkin\": \"2024-02-01\", \"checkout\": \"2024-02-15\"}, \"additionalneeds\": \"Lunch\" }";
    Response response = given()
            .contentType(ContentType.JSON)
            .header("Cookie", "token=" + authToken)
            .body(updatedRequestBody)
            .log().all()
            .when()
            .put("/booking/")
            .then()
            .log().all()
            .statusCode(404)
            .extract().response();

    String ActualResponse =response.prettyPrint();
    System.out.println("Negative test : " + response.prettyPrint());
    Assert.assertEquals(response.getStatusCode(),404,"ID is required field");
    Assert.assertTrue(ActualResponse.contains("Not Found"),"This is valid response since ID is not provided");

}



}



