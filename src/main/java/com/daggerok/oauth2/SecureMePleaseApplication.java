package com.daggerok.oauth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by mak on 4/30/16.
 */
@SpringBootApplication
public class SecureMePleaseApplication {

    // see tests for details: com.daggerok.oauth2.SecureMePleaseApplicationTests

    /* //60 seconds expiration time token example
    1.
    curl rest-user:cli@localhost:8080/uaa/oauth/token \
        -d grant_type=password \
        -d username=max \
        -d password=pass |pj
    {
        "access_token": "c6517893-9ce0-4fef-9e71-5e108fe3a504",
        "expires_in": 59,
        "refresh_token": "6c4fc789-1534-4455-a0d4-1321f89d4241",
        "scope": "read write",
        "token_type": "bearer"
    }
    2.
    export TOKEN=c6517893-9ce0-4fef-9e71-5e108fe3a504
    3.
    curl -ivH 'content-type:application/json' \
        -H "authorization:bearer $TOKEN" \
        localhost:8080/uaa/api/domains \
        -d '{"content":"some body"}'
    {
      "content" : "some body",
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/uaa/api/domains/5724d3a9bee8baa0c2760d14"
        },
        "domain" : {
          "href" : "http://localhost:8080/uaa/api/domains/5724d3a9bee8baa0c2760d14"
        }
      }
    }
    4.
    wait 1 minute
    5. repeat last command and receive unauthorized response
    {
        "error":"invalid_token",
        "error_description":"Access token expired: a6838aa7-dfc9-437f-a24d-9af89a293bb9"
     }
     */

    public static void main(String[] args) {
        SpringApplication.run(SecureMePleaseApplication.class, args);
    }
}








