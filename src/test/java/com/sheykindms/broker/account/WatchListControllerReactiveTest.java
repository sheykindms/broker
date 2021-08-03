package com.sheykindms.broker.account;

import com.sheykindms.broker.model.Symbol;
import com.sheykindms.broker.model.WatchList;
import com.sheykindms.broker.inmemorystore.InMemoryAccountStore;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class WatchListControllerReactiveTest {
  private static final UUID TEST_ACCOUNT_ID = UUID.randomUUID();

  @Inject EmbeddedApplication application;

  @Inject
  @Client("/")
  JWTWatchListClient client;

  @Inject InMemoryAccountStore store;

  @Test
  void returnsEmptyWatchListForAccount() {
    var result =
        client.retrieveWatchList(getAuthorizationHeader(), TEST_ACCOUNT_ID).singleOrError();
    assertTrue(result.blockingGet().getSymbols().isEmpty());
    assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
  }

  @Test
  void returnsWatchListForAccount() {
    var symbols = Stream.of("AAPL", "AMZN", "NFXL").map(Symbol::new).collect(Collectors.toList());
    WatchList watchList = new WatchList(symbols);
    store.updateWatchList(TEST_ACCOUNT_ID, watchList);
    var result =
        client
            .retrieveWatchList(getAuthorizationHeader(), TEST_ACCOUNT_ID)
            .singleOrError()
            .blockingGet();
    assertEquals(3, result.getSymbols().size());
    assertEquals(3, store.getWatchList(TEST_ACCOUNT_ID).getSymbols().size());
  }

  @Test
  void canUpdateWatchListForAccount() {
    var symbols = Stream.of("AAPL", "AMZN", "NFXL").map(Symbol::new).collect(Collectors.toList());
    WatchList watchList = new WatchList(symbols);
    store.updateWatchList(TEST_ACCOUNT_ID, watchList);
    var added = client.updateWatchList(getAuthorizationHeader(), watchList, TEST_ACCOUNT_ID);
    assertEquals(HttpStatus.OK, added.getStatus());
    assertEquals(watchList, store.getWatchList(TEST_ACCOUNT_ID));
  }

  @Test
  void canDeleteWatchListForAccount() {
    var symbols = Stream.of("AAPL", "AMZN", "NFXL").map(Symbol::new).collect(Collectors.toList());
    WatchList watchList = new WatchList(symbols);
    store.updateWatchList(TEST_ACCOUNT_ID, watchList);
    assertFalse(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());

    var deleted = client.deleteWatchList(getAuthorizationHeader(), TEST_ACCOUNT_ID);
    assertEquals(HttpStatus.OK, deleted.getStatus());
    assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
  }

  private String getAuthorizationHeader() {
    return "Bearer " + givenMyUserLoggedIn().getAccessToken();
  }

  private BearerAccessRefreshToken givenMyUserLoggedIn() {
    return client.login(new UsernamePasswordCredentials("my-user", "secret"));
  }
}
