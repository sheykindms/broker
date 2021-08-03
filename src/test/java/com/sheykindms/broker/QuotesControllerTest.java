package com.sheykindms.broker;

import com.sheykindms.broker.error.QuoteErrorInfo;
import com.sheykindms.broker.model.Quote;
import com.sheykindms.broker.model.Symbol;
import com.sheykindms.broker.inmemorystore.InMemoryStore;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static io.micronaut.http.HttpRequest.GET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class QuotesControllerTest {
  @Inject EmbeddedApplication application;

  @Inject
  @Client("/quotes")
  RxHttpClient client;

  @Inject InMemoryStore store;

  @Test
  void returnsQuoteCorrespondingToGivenSymbol() {
    final Quote apple = store.update(getRandomQuote("AAPL"));
    final Quote fb = store.update(getRandomQuote("FB"));

    final Quote appleResult = client.toBlocking().retrieve(GET("/AAPL"), Quote.class);
    assertThat(apple).usingRecursiveComparison().isEqualTo(appleResult);

    final Quote fbResult = client.toBlocking().retrieve(GET("/FB"), Quote.class);
    assertThat(fb).usingRecursiveComparison().isEqualTo(fbResult);
  }

  private Quote getRandomQuote(String symbolValue) {
    return Quote.builder()
        .symbol(new Symbol(symbolValue))
        .bid(getRandomValue())
        .ask(getRandomValue())
        .volume(getRandomValue())
        .build();
  }

  @Test
  void returnsNotFoundOnUnsupportedSymbol() {
    try {
      client
          .toBlocking()
          .retrieve(
              GET("/UNSUPPORTED_SYMBOL"),
              Argument.of(Quote.class),
              Argument.of(QuoteErrorInfo.class));
    } catch (HttpClientResponseException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getResponse().getStatus());

      final Optional<QuoteErrorInfo> quoteError = e.getResponse().getBody(QuoteErrorInfo.class);
      assertTrue(quoteError.isPresent());
      assertEquals(404, quoteError.get().getStatus());
      assertEquals("NOT_FOUND", quoteError.get().getError());
      assertEquals("quote for the symbol is not available", quoteError.get().getMessage());
      assertEquals("/quotes/UNSUPPORTED_SYMBOL", quoteError.get().getPath());
    }
  }

  private BigDecimal getRandomValue() {
    return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 100));
  }
}
