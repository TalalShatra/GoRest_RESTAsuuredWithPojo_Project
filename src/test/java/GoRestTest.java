import Pojo.GoRestUser;
import Pojo.GoRestPost;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/*1- Create new User
2- Create User Negative Test
3- Create Post (you need to use user_id from the user you created)
4- Edit Post
5- Delete Post
6- Delete User
7- Delete User Negative Test

Note: You'll have 2 POJO classes 'GoRestUser' and 'GoRestPost'!

 */

public class GoRestTest {

    private GoRestUser user;

    private GoRestPost post;
    private RequestSpecification reqSpec;

    @BeforeClass
    public void setup(){

        RestAssured.baseURI = "https://gorest.co.in";

        reqSpec = given()
                .header("Authorization", "Bearer 53d95bec442a8f9343db9cd387b27ecb9adbadf050d36e60ca77da2430b9d4c2")
                .contentType(ContentType.JSON);

        user = new GoRestUser();
        user.setName("sargeo ramos");
        user.setEmail("sargeoramos@realmadrid.com");
        user.setGender("male");
        user.setStatus("active");

        post = new GoRestPost();
        post.setId(user.getId());
        post.setTitle("Java Language");
        post.setBody("Java is a programming language and computing platform first released by Sun Microsystems in 1995.");

    }
    @Test
    public void createUserTest() {

        user.setId(given()
                .spec(reqSpec)
                .body(user)
                .when()
                .post("/public/v2/users")
                .then()
                .log().body()
                .statusCode(201)
                .body("name", equalTo(user.getName()))
                .extract().jsonPath().getString("id"));
    }
    @Test(dependsOnMethods = "createUserTest")
    public void createUserNegativeTest() {

        given()
                .spec(reqSpec)
                .body(user)
                .when()
                .post("/public/v2/users")
                .then()
                .log().body()
                .statusCode(422);
    }

    @Test(dependsOnMethods = "createUserNegativeTest")
    public void createPostTest() {

        post.setPostId(given()
                .spec(reqSpec)
                .body(post)
                .when()
                .post("/public/v2/users/" + user.getId() + "/posts")
                .then()
                .log().body()
                .statusCode(201)
                .extract().jsonPath().getString("id"));
    }
    @Test(dependsOnMethods = "createPostTest")
    public void updatePostTest() {

        HashMap<String, String> body = new HashMap<>();
        body.put("title", "What is Java");

        given()
                .spec(reqSpec)
                .body(body)
                .when()
                .put("/public/v2/posts/" + post.getPostId())
                .then()
                .log().body()
                .statusCode(200);
    }
    @Test(dependsOnMethods = "updatePostTest")
    public void deletePostTest() {

        given()
                .spec(reqSpec)
                .when()
                .delete("/public/v2/posts/" + post.getPostId())
                .then()
                .log().body()
                .statusCode(204);
    }

   @Test(dependsOnMethods = "deletePostTest")
   public void deleteUserTest() {

       given()
               .spec(reqSpec)
               .when()
               .delete("/public/v2/users/" + user.getId())
               .then()
               .log().body()
               .statusCode(204);
   }

    @Test(dependsOnMethods = "deleteUserTest")
    public void deleteUserNegativeTest() {

        given()
                .spec(reqSpec)
                .when()
                .delete("/public/v2/users/" + user.getId())
                .then()
                .log().body()
                .statusCode(404);
    }







}
