package client;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class PostClient {
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    public Response getAllPosts() {
        return RestAssured.get(BASE_URL + "/posts");
    }

    public Response getPostById(int id) {
        return RestAssured.get(BASE_URL + "/posts/" + id);
    }

    public Response createPost(String title, String body, int userId) {
        String payload = String.format("{\"title\":\"%s\",\"body\":\"%s\",\"userId\":%d}", title, body, userId);
        return RestAssured.given()
                .contentType("application/json")
                .body(payload)
                .post(BASE_URL + "/posts");
    }
}
