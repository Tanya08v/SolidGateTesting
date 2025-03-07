package com.solidgate;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import static com.google.gson.JsonParser.parseString;
import static com.solidgate.RequestBodyStubs.PAYMENT_INIT_BODY;
import static com.solidgate.AuthorisationUtil.PUBLIC_KEY;
import static com.solidgate.AuthorisationUtil.generateSignature;

public class PaymentPageTest {
    private static final String BASE_URL = "https://payment-page.solidgate.com/";
    private static final String ORDER_ID = "autotest_1741250764909tfq8m_testOrders";
    private static final String AMOUNT = "10.99";
    private static final String CURRENCY = "USD";
    private static final String CARD = "4067429974719265";
    private static final String EXPIRATION_DATE = "12/34";
    private static final String CVV = "123";
    private static final String CARD_HOLDER = "Name Surname";



    private static ChromeDriver driver;
    private static URL paymentPageUrl;

    @BeforeAll
    public static void setUp() throws IOException, ParseException {
        System.setProperty("webdriver.chrome.driver", "/Users/tatana/Desktop/solid/chromedriver");


        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(BASE_URL + "api/v1/init");
        httpPost.setEntity(new StringEntity(PAYMENT_INIT_BODY));

        httpPost.setHeader("merchant", PUBLIC_KEY);
        httpPost.setHeader("Signature", generateSignature(PAYMENT_INIT_BODY));

        var response = httpClient.execute(httpPost);
        var json = EntityUtils.toString(response.getEntity());
        paymentPageUrl = URI.create(parseString(json).getAsJsonObject().get("url").getAsString()).toURL();
    }

    @AfterAll
    static void afterAll() {
        // Clean up
    }

    @Test
    public void testPaymentSuccessful() throws InterruptedException {
        driver = new ChromeDriver();
        driver.get(paymentPageUrl.toString());

        Thread.sleep(2000);

        driver.findElement(By.id("ccnumber")).sendKeys(CARD);
        Thread.sleep(4000);

        driver.findElement(By.name("cardExpiryDate")).sendKeys(EXPIRATION_DATE);
        Thread.sleep(4000);

        driver.findElement(By.id("cvv2")).sendKeys(CVV);
        Thread.sleep(4000);

        driver.findElement(By.name("cardHolder")).sendKeys(CARD_HOLDER);
        Thread.sleep(4000);

        driver.quit();
    }

    @Test
    public void testPaymentIncorrectCardValidation() throws InterruptedException {
        driver = new ChromeDriver();
        driver.get(paymentPageUrl.toString());

        Thread.sleep(2000);

        driver.findElement(By.id("ccnumber")).sendKeys("CARD");
        Thread.sleep(4000);

        driver.findElement(By.name("cardExpiryDate")).sendKeys("test");
        Thread.sleep(4000);

        driver.findElement(By.id("cvv2")).sendKeys("test");
        Thread.sleep(4000);

        driver.findElement(By.name("cardHolder")).sendKeys("test");
        Thread.sleep(4000);

        driver.quit();
    }

    @Test
    public void testPaymentEmptyFields() throws InterruptedException {
        driver = new ChromeDriver();
        driver.get(paymentPageUrl.toString());

        Thread.sleep(2000);

        driver.findElement(By.id("ccnumber")).sendKeys("");
        Thread.sleep(4000);

        driver.findElement(By.name("cardExpiryDate")).sendKeys("");
        Thread.sleep(4000);

        driver.findElement(By.id("cvv2")).sendKeys("");
        Thread.sleep(4000);

        driver.findElement(By.name("cardHolder")).sendKeys("");
        Thread.sleep(4000);

        driver.quit();
    }
}