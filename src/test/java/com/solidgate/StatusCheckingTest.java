package com.solidgate;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.google.gson.JsonParser.parseString;
import static com.solidgate.util.AuthorisationUtil.PUBLIC_KEY;
import static com.solidgate.util.AuthorisationUtil.generateSignature;
import static com.solidgate.RequestBodyStubs.ORDER_STATUS_BODY;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class StatusCheckingTest {
    private static final String STATUS_URL = "https://pay.solidgate.com/api/v1/status";

    @Test
    public void returnCorrectCurrencyAndAmount() throws IOException, ParseException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(STATUS_URL);
        httpPost.setEntity(new StringEntity(ORDER_STATUS_BODY));

        httpPost.setHeader("merchant", PUBLIC_KEY);
        httpPost.setHeader("Signature", generateSignature(ORDER_STATUS_BODY));

        var response = httpClient.execute(httpPost);
        var json = EntityUtils.toString(response.getEntity());
        var orderData = parseString(json)
                .getAsJsonObject()
                .get("order")
                .getAsJsonObject();
        var amount = orderData
                .get("amount")
                .getAsInt();
        var currency = orderData.get("currency").getAsString();

        assertEquals(amount, 800);
        assertEquals(currency, "USD");
    }

}
