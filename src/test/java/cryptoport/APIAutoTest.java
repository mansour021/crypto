package cryptoport;

import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import java.util.Random;


public class APIAutoTest {

    private final String BASE_URI = "https://mic-vm-dev.westeurope.cloudapp.azure.com/v1/CRYPTOPORTV6";
    private final String ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjJPNzBSd3VHTFR0T0xEcTd5STBRUCJ9.eyJpc3MiOiJodHRwczovL2NyeXB0b3BvcnQtZGV2LmV1LmF1dGgwLmNvbS8iLCJzdWIiOiJhdXRoMHw2NTY4OTVlODAzMDA5NjZhZDNiNzk0YTgiLCJhdWQiOlsiaHR0cHM6Ly9taWMtdm0tZGV2Lndlc3RldXJvcGUuY2xvdWRhcHAuYXp1cmUuY29tL3YxIiwiaHR0cHM6Ly9jcnlwdG9wb3J0LWRldi5ldS5hdXRoMC5jb20vdXNlcmluZm8iXSwiaWF0IjoxNzA4MjUyODA5LCJleHAiOjE3MDgzMzkyMDksImF6cCI6ImJta3Fna3NmN1N0N3pnZDhyYXUyNFV4Qm5mbXVvU2NLIiwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCJ9.YGP7IevMR1XhxZzyqZaaoj_oDyvpAZRujpZgtjN9LfmIj_LMka3Zd6H43wsT7BnV_oX6zqy_dxVsUb7RwXy-uaXAl_QVBTiXZ8t3edFmXbhpF5zMPIvPkMpz2Gl6Cvv_cvDgGYwSnldKi8qWL4tPtkJUwKg_QEVkDbHH-rVxVLza6F34GiUSwqS6n5fj6EBobOXizPReGoulDnHTaAvEzGV6Wq7Pb1c7fyy9tQZXzmTj7ulRsv1w2YxwIcDIOZpWW64GBWwA-S9BmuubJSX1LdfTN7EMhp43YkraRDJa7p2d2a1m1yT9V5-ywPBu7L5IcSjFFz_Gx6T63JS749a55A"; // Access token
	private static String extractedAddress;

    
    @Test (priority=1)
    public void testGetAccountDetails() {
        RestAssured.baseURI = BASE_URI; // Base URI

        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .contentType(ContentType.JSON)
            .when()
                .get("/access/system/accounts/me")
            .then()
                .extract().response();

        assertEquals(response.getStatusCode(), 200, "Unexpected status code");

        // Print the response 
        System.out.println("GET Response Code: " + response.getStatusCode());
        System.out.println("GET Response Body: " + response.getBody().asString());
    }
    
    
    @Test(priority = 2)
    public void testListAllTokens() {
        RestAssured.baseURI = BASE_URI;

        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .contentType(ContentType.JSON)
            .when()
                .get("/tokens?networkId=1393")
            .then()
                .extract().response();

        assertEquals(response.getStatusCode(), 200, "Unexpected status code");

        // Print the response 
        System.out.println("GET List All Tokens Response Code: " + response.getStatusCode());
        System.out.println("GET List All Tokens Response Body: " + response.getBody().asString());
    }
    
    @Test (priority=3)
    public void testPostTokenDeployment() {
        RestAssured.baseURI = BASE_URI;
        
     // Generate random values for name and symbol
        String randomName = generateRandomString(10);
        String randomSymbol = generateRandomString(5);
               

        // JSON body of the token deployment request
        String jsonBody = "[{\"token\":{\"admin\":\"0x1Ba4F352DEB45bf56C14B2E3fc5Ba052C468E798\",\"cap\":1000000,\"controller\":\"0x1Ba4F352DEB45bf56C14B2E3fc5Ba052C468E798\",\"granularity\":1,\"issuer\":\"0x1Ba4F352DEB45bf56C14B2E3fc5Ba052C468E798\",\"moduleEditor\":\"0x1Ba4F352DEB45bf56C14B2E3fc5Ba052C468E798\",\"name\":\"" + randomName + "\",\"redeemer\":\"0x1Ba4F352DEB45bf56C14B2E3fc5Ba052C468E798\",\"symbol\":\"" + randomSymbol + "\"}}]";

        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .contentType(ContentType.JSON)
                .body(jsonBody)
            .when()
                .post("/tokens/1393/deployv2")
            .then()
                .extract().response();

        assertEquals(response.getStatusCode(), 201, "Unexpected status code");
        
        // Extract the "Address" of the deployed asset in a new varialbe 
        String address = response.jsonPath().getString("[0].address");
        
        // Print the extracted address
        System.out.println("Extracted Address: " + address);
        extractedAddress = address;
        
        // Print the response details
        System.out.println("POST Response Code: " + response.getStatusCode());
        System.out.println("POST Response Body: " + response.getBody().asString());
    }
    
    private static String generateRandomString(int length) {
        // Define the characters for the random string
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        // StringBuilder 
        StringBuilder randomString = new StringBuilder(length);

        // Generate random characters
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(allowedChars.length());
            char randomChar = allowedChars.charAt(index);
            randomString.append(randomChar);
        }

        return randomString.toString();
    }
    
    @Test(priority = 4)
    public void testGetTokenInfo() {
        RestAssured.baseURI = BASE_URI;

        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .contentType(ContentType.JSON)
            .when()
                .get("/tokens/" + extractedAddress)
            .then()
                .extract().response();

        assertEquals(response.getStatusCode(), 200, "Unexpected status code");

        // Print the response details
        System.out.println("GET Token Info Response Code: " + response.getStatusCode());
        System.out.println("GET Token Info Response Body: " + response.getBody().asString());
    }
    
    @Test(priority = 5)
    public void testListTokenHolders() {
        RestAssured.baseURI = BASE_URI;

        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .contentType(ContentType.JSON)
            .when()
                .get("/tokens/" + extractedAddress + "/holders")
            .then()
                .extract().response();

        assertEquals(response.getStatusCode(), 200, "Unexpected status code");

        // Print the response details
        System.out.println("GET List Token Holders Response Code: " + response.getStatusCode());
        System.out.println("GET List Token Holders Response Body: " + response.getBody().asString());
    }

	@Test(priority = 6)
    public void testDeleteToken() {
        // Ensure that the extractedAddress is not null or empty
        assertNotNull(extractedAddress, "Extracted address is null or empty");

        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
            .when()
                .delete("/tokens/" + extractedAddress);

        assertEquals(response.getStatusCode(), 202, "Unexpected status code");

        // Print the response details
        System.out.println("DELETE Response Code: " + response.getStatusCode());
        System.out.println("DELETE Response Body: " + response.getBody().asString());
    }
	
    @Test(priority = 7)
    public void testMintTokenCall() {
        RestAssured.baseURI = BASE_URI;

        // JSON body for minting new token
        String jsonBody = "{\n" +
                "  \"contractAddress\": \"0xbf006A59c851eD89E185F50dF7952F1F3cAABc42\",\n" +
                "  \"from\": \"0x1Ba4F352DEB45bf56C14B2E3fc5Ba052C468E798\",\n" +
                "  \"method\": \"mint\",\n" +
                "  \"parameters\": [\n" +
                "    \"0x1Ba4F352DEB45bf56C14B2E3fc5Ba052C468E798\",\n" +
                "    20\n" +
                "     ]\n" +
                "}";

        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .contentType(ContentType.JSON)
                .body(jsonBody)
            .when()
                .post("/tokens/1393/call")
            .then()
                .extract().response();

        assertEquals(response.getStatusCode(), 200, "Unexpected status code");

        // Print the response details
        System.out.println("POST Token Call Response Code: " + response.getStatusCode());
        System.out.println("POST Token Call Response Body: " + response.getBody().asString());
    }
    
    
    @Test(priority = 8)
    public void testPostTokenTransfer() {
        RestAssured.baseURI = BASE_URI;

        // Specify the JSON body for transferring tokens from one account to the other
        String jsonBody = "{\n" +
                "  \"contractAddress\": \"0xbf006A59c851eD89E185F50dF7952F1F3cAABc42\",\n" +
                "  \"from\": \"0x1Ba4F352DEB45bf56C14B2E3fc5Ba052C468E798\",\n" +
                "  \"method\": \"transfer\",\n" +
                "  \"parameters\": [\n" +
                "    \"0x264a02942f0Ad25bE3e5db6420F4C323B585769e\",\n" +
                "    10\n" +
                "     ]\n" +
                "}";

        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .contentType(ContentType.JSON)
                .body(jsonBody)
            .when()
                .post("/tokens/1393/call")
            .then()
                .extract().response();

        assertEquals(response.getStatusCode(), 200, "Unexpected status code");

        // Print the response details
        System.out.println("POST Token Transfer Response Code: " + response.getStatusCode());
        System.out.println("POST Token Transfer Response Body: " + response.getBody().asString());
    }  
    
    @Test(priority = 9)
    public void testBurnTokenCall() {
        RestAssured.baseURI = BASE_URI;

        // JSON body for burning token from an account 
        String jsonBody = "{\n" +
                "  \"contractAddress\": \"0xbf006A59c851eD89E185F50dF7952F1F3cAABc42\",\n" +
                "  \"from\": \"0x1Ba4F352DEB45bf56C14B2E3fc5Ba052C468E798\",\n" +
                "  \"method\": \"burn\",\n" +
                "  \"parameters\": [\n" +
                "    \"0x1Ba4F352DEB45bf56C14B2E3fc5Ba052C468E798\",\n" +
                "    10\n" +
                "     ]\n" +
                "}";

        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .contentType(ContentType.JSON)
                .body(jsonBody)
            .when()
                .post("/tokens/1393/call")
            .then()
                .extract().response();

        assertEquals(response.getStatusCode(), 200, "Unexpected status code");

        // Print the response details
        System.out.println("POST Token Call Response Code: " + response.getStatusCode());
        System.out.println("POST Token Call Response Body: " + response.getBody().asString());
    }
        

}

