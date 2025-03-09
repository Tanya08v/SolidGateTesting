package com.solidgate;

public interface RequestBodyStubs {
    String PAYMENT_INIT_BODY = """
        {
           "order":{
              "order_id":"autotest_1741250824007tqjsx_testOrders",
              "amount":1020,
              "currency":"EUR",
              "order_description":"Premium package",
              "order_items":"item 1 x 10, item 2 x 30",
              "order_date":"2024-12-21 11:21:30",
              "order_number":9,
              "type":"auth",
              "settle_interval":0,
              "retry_attempt":3,
              "force3ds":false,
              "google_pay_allowed_auth_methods":[
                 "PAN_ONLY"
              ],
              "customer_date_of_birth":"1988-11-21",
              "customer_email":"example@example.com",
              "customer_first_name":"Nikola",
              "customer_last_name":"Tesla",
              "customer_phone":"+10111111111",
              "traffic_source":"facebook",
              "transaction_source":"main_menu",
              "purchase_country":"USA",
              "geo_country":"USA",
              "geo_city":"New Castle",
              "language":"pt",
              "website":"https://solidgate.com",
              "order_metadata":{
                 "coupon_code":"NY2024",
                 "partner_id":"123989"
              },
              "success_url":"http://merchant.example/success",
              "fail_url":"http://merchant.example/fail"
           },
           "page_customization":{
              "public_name":"Public Name",
              "order_title":"Order Title",
              "order_description":"Premium package",
              "payment_methods":[
                 "paypal"
              ],
              "button_font_color":"#FFFFFF",
              "button_color":"#00816A",
              "font_name":"Open Sans",
              "is_cardholder_visible":true,
              "terms_url":"https://solidgate.com/terms",
              "back_url":"https://solidgate.com"
           }
        }
      """;
    String ORDER_STATUS_BODY = """
        {
            "order_id": "autotest_1741250824007tqjsx_testOrders"
        }""";

}
