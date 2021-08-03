package com.sheykindms;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
    info =
        @Info(
            title = "stock-broker",
            version = "0.1",
            description = "Stock Broker App",
            license = @License(name = "MIT")))
public class Application {

  public static void main(String[] args) {
    Micronaut.run(Application.class, args);
  }
}
