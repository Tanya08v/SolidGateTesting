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
import static com.solidgate.util.AuthorisationUtil.PUBLIC_KEY;
import static com.solidgate.util.AuthorisationUtil.generateSignature;
import static java.lang.Thread.sleep;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.Assert.assertEquals;

public class PaymentPageTest {
    private static final String BASE_URL = "https://payment-page.solidgate.com/";
    private static final String CARD = "4067429974719265";
    private static final String EXPIRATION_DATE = "12/34";
    private static final String CVV = "123";
    private static final String CARD_HOLDER = "Name Surname";

    private static ChromeDriver driver;
    private static URL paymentPageUrl;

    @BeforeAll
    public static void beforeAll() throws IOException, ParseException {
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

    @BeforeEach
    void setUp() throws InterruptedException {
        driver = new ChromeDriver();
        driver.get(paymentPageUrl.toString());
        sleep(2000);
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    @Test
    public void testPaymentSuccessful() {
        driver.findElement(By.id("ccnumber")).sendKeys(CARD);
        driver.findElement(By.name("cardExpiryDate")).sendKeys(EXPIRATION_DATE);
        driver.findElement(By.id("cvv2")).sendKeys(CVV);
        driver.findElement(By.name("cardHolder")).sendKeys(CARD_HOLDER);

        submitForm();
    }

    @Test
    public void testPaymentIncorrectDataValidation() {
        driver.findElement(By.id("ccnumber")).sendKeys("CARD");
        driver.findElement(By.name("cardExpiryDate")).sendKeys("test");
        driver.findElement(By.id("cvv2")).sendKeys("test");
        driver.findElement(By.name("cardHolder")).sendKeys("test");

        submitForm();

        assertEquals(driver.findElementById("ccnumber").getAttribute("value"), EMPTY);
        assertEquals(driver.findElementByName("cardExpiryDate").getAttribute("value"), EMPTY);
        assertEquals(driver.findElementById("cvv2").getAttribute("value"), EMPTY);
        assertEquals(driver.findElementByName("cardHolder").getAttribute("value"), "TEST");
    }

    @Test
    public void testPaymentEmptyFields() {
        driver.findElement(By.id("ccnumber")).sendKeys("");
        driver.findElement(By.name("cardExpiryDate")).sendKeys("");
        driver.findElement(By.id("cvv2")).sendKeys("");
        driver.findElement(By.name("cardHolder")).sendKeys("");

        submitForm();

        assertEquals(driver.findElement(By.id("cardNumber_error-text")).getText(),
                "Verifique o número do cartão");
        assertEquals(driver.findElement(By.id("cardExpiryDate_error-text")).getText(),
                "Insira a data de validade do seu cartão");
        assertEquals(driver.findElement(By.id("cardCvv_error-text")).getText(),
                "Insira o código de segurança do cartão de 3 dígitos");
        assertEquals(driver.findElement(By.id("cardHolder_error-text")).getText(),
                "Campo obrigatório");
    }

    @Test
    public void testPaymentErrorCardMessage() {
        driver.findElement(By.id("ccnumber")).sendKeys("1111");

        submitForm();

        assertEquals(driver.findElement(By.id("cardNumber_error-text")).getText(),
                "Verifique o número do cartão");
    }


    @Test
    public void testPaymentErrorCardExpiryMessage() {
        driver.findElement(By.name("cardExpiryDate")).sendKeys("11/12");

        submitForm();

        assertEquals(driver.findElement(By.id("cardExpiryDate_error-text")).getText(),
                "A data de validade já passou");
    }

    @Test
    public void testPaymentErrorCardExpiryEmptyMessage() {
        driver.findElement(By.name("cardExpiryDate")).sendKeys("");

        submitForm();

        assertEquals(driver.findElement(By.id("cardExpiryDate_error-text")).getText(),
                "Insira a data de validade do seu cartão");
    }

    @Test
    public void testPaymentErrorCVCMessage() {
        driver.findElement(By.id("cvv2")).sendKeys("1");

        submitForm();

        assertEquals(driver.findElement(By.id("cardCvv_error-text")).getText(),
                "Insira o código de segurança do cartão de 3 dígitos");
    }

    @Test
    public void testPaymentErrorCVCEmptyMessage() {
        driver.findElement(By.id("cvv2")).sendKeys("");

        submitForm();

        assertEquals(driver.findElement(By.id("cardCvv_error-text")).getText(),
                "Insira o código de segurança do cartão de 3 dígitos");
    }

    @Test
    public void testPaymentErrorNameOnCardMessage() {
        driver.findElement(By.name("cardHolder")).sendKeys("0000");

        submitForm();

        assertEquals(driver.findElement(By.id("cardHolder_error-text")).getText(),
                "Campo obrigatório");
    }

    @Test
    public void testPaymentErrorIncorrectNameOnCardMessage() {
        driver.findElement(By.name("cardHolder")).sendKeys("W");

        submitForm();

        assertEquals(driver.findElement(By.id("cardHolder_error-text")).getText(),
                "O nome do titular do cartão é inválido");
    }

    @Test
    public void testPaymentEmptyNameOnCardMessage() {
        driver.findElement(By.name("cardHolder")).sendKeys("");

        submitForm();

        assertEquals(driver.findElement(By.id("cardHolder_error-text")).getText(),
                "Campo obrigatório");
    }

    private static void submitForm() {
        driver.findElementByCssSelector("form button[type='submit']").click();
    }
}