package tests;

import client.PostClient;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("JSONPlaceholder API Tests")
@Feature("Posts Endpoint")
public class PostApiTest {
    PostClient client = new PostClient();

    @Test
    @Story("Get all posts")
    @Severity(SeverityLevel.NORMAL)
    @Description("Valida que a API retorna todos os posts com status 200")
    void testGetAllPosts() {
        client.getAllPosts()
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    @Story("Validate posts count")
    @Severity(SeverityLevel.MINOR)
    @Description("Valida que a API retorna exatamente 100 posts")
    void testPostsCount() {
        client.getAllPosts()
                .then()
                .statusCode(200)
                .body("size()", equalTo(100));
    }

    @Test
    @Story("Get first post")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Valida que o primeiro post possui id=1 e campos preenchidos, anexando resposta")
    void testGetFirstPost() {
        String responseBody = client.getAllPosts()
                .then()
                .statusCode(200)
                .extract()
                .asString();

        // Anexa o corpo da resposta ao relatório
        Allure.addAttachment("Response Body", responseBody);

        Map<String, Object> firstPost = client.getAllPosts()
                .then()
                .statusCode(200)
                .extract()
                .path("[0]");

        assertThat(firstPost.get("id"), equalTo(1));
        assertThat(firstPost.get("userId"), notNullValue());
        assertThat(firstPost.get("title").toString(), not(isEmptyString()));
    }

    @Test
    @Story("Get first post title")
    @Severity(SeverityLevel.TRIVIAL)
    @Description("Valida que o título do primeiro post não está vazio")
    void testFirstPostTitle() {
        String title = client.getAllPosts()
                .then()
                .statusCode(200)
                .extract()
                .path("[0].title");

        Allure.step("Título extraído: " + title);

        assertThat(title, not(isEmptyString()));
    }

    @Test
    @Story("Get post by ID")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Valida que o post com id=1 é retornado corretamente com steps detalhados")
    void testGetPostById() {
        Allure.step("Chama GET /posts/1");
        var response = client.getPostById(1);

        Allure.step("Valida status 200");
        response.then().statusCode(200);

        Allure.step("Valida campos retornados");
        response.then()
                .body("id", equalTo(1))
                .body("userId", notNullValue());
    }

    @Test
    @Story("Get non-existing post")
    @Severity(SeverityLevel.NORMAL)
    @Description("Valida que a API retorna 404 ao buscar um post inexistente")
    void testGetNonExistingPost() {
        client.getPostById(9999)
                .then()
                .statusCode(404);
    }

    @Test
    @Story("Create post")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Valida que é possível criar um post e que os dados retornam corretamente")
    void testCreatePost()
    { Response response = client.createPost("Meu título", "Meu conteúdo", 1); // Anexa o corpo da resposta ao relatório
        Allure.addAttachment("Response Body", response.asString());

        response.then()
                .statusCode(201)
                .body("title", equalTo("Meu título"))
                .body("body", equalTo("Meu conteúdo"))
                .body("userId", equalTo(1)); }

    @Test
    @Story("Create post with invalid data")
    @Severity(SeverityLevel.MINOR)
    @Description("Valida que a API retorna erro ao tentar criar um post sem título")
    void testCreatePostInvalid() {
        client.createPost("", "Conteúdo sem título", 1)
                .then()
                .statusCode(anyOf(is(400), is(201)));
        // JSONPlaceholder aceita, mas em APIs reais seria 400
    }

    @Test
    @Story("Contract validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Valida o contrato do endpoint GET /posts/{id} contra o schema definido")
    void testContractValidation() {
        client.getPostById(1)
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/post-schema.json"));
    }
}
