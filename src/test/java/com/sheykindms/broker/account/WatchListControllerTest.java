package com.sheykindms.broker.account;

import com.sheykindms.broker.model.Symbol;
import com.sheykindms.broker.model.WatchList;
import com.sheykindms.broker.inmemorystore.InMemoryAccountStore;
import io.micronaut.http.*;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.micronaut.http.HttpRequest.*;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class WatchListControllerTest {
  @Inject
  @Client("/")
  RxHttpClient client;

  private static final UUID TEST_ACCOUNT_ID = UUID.randomUUID();
  private static final String ACCOUNT_WATCHLIST = "/account/watchlist/";

  @Inject EmbeddedApplication application;

  @Inject InMemoryAccountStore store;

  @Test
  void unauthorizedAccessIsForbidden() {
    try {
      client.toBlocking().retrieve(ACCOUNT_WATCHLIST);
      fail("Should fail if no exception thrown");
    } catch (HttpClientResponseException e) {
      assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
    }
  }

  @Test
  void returnsEmptyWatchListForAccount() {
    final BearerAccessRefreshToken token = givenMyUserLoggedIn();

    var request =
        GET(ACCOUNT_WATCHLIST + TEST_ACCOUNT_ID)
            .accept(MediaType.APPLICATION_JSON)
            .bearerAuth(token.getAccessToken());
    final WatchList result = client.toBlocking().retrieve(request, WatchList.class);
    assertTrue(result.getSymbols().isEmpty());
    assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
  }

  @Test
  void returnsWatchListForAccount() {
    final BearerAccessRefreshToken token = givenMyUserLoggedIn();

    final List<Symbol> symbols =
        Stream.of("AAPL", "AMZN", "NFXL").map(Symbol::new).collect(Collectors.toList());
    WatchList watchList = new WatchList(symbols);
    store.updateWatchList(TEST_ACCOUNT_ID, watchList);

    var request =
        GET(ACCOUNT_WATCHLIST + TEST_ACCOUNT_ID)
            .accept(MediaType.APPLICATION_JSON)
            .bearerAuth(token.getAccessToken());

    final WatchList result = client.toBlocking().retrieve(request, WatchList.class);
    assertEquals(3, result.getSymbols().size());
    assertEquals(3, store.getWatchList(TEST_ACCOUNT_ID).getSymbols().size());
  }

  @Test
  void canUpdateWatchListForAccount() {
    final BearerAccessRefreshToken token = givenMyUserLoggedIn();

    final List<Symbol> symbols =
        Stream.of("AAPL", "AMZN", "NFXL").map(Symbol::new).collect(Collectors.toList());
    WatchList watchList = new WatchList(symbols);
    store.updateWatchList(TEST_ACCOUNT_ID, watchList);

    var request =
        PUT(ACCOUNT_WATCHLIST + TEST_ACCOUNT_ID, watchList)
            .accept(MediaType.APPLICATION_JSON)
            .bearerAuth(token.getAccessToken());

    final HttpResponse<Object> added = client.toBlocking().exchange(request);
    assertEquals(HttpStatus.OK, added.getStatus());
    assertEquals(watchList, store.getWatchList(TEST_ACCOUNT_ID));
  }

  @Test
  void canDeleteWatchListForAccount() {
    final BearerAccessRefreshToken token = givenMyUserLoggedIn();

    final List<Symbol> symbols =
        Stream.of("AAPL", "AMZN", "NFXL").map(Symbol::new).collect(Collectors.toList());
    WatchList watchList = new WatchList(symbols);
    store.updateWatchList(TEST_ACCOUNT_ID, watchList);
    assertFalse(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());

    var request =
        DELETE(ACCOUNT_WATCHLIST + TEST_ACCOUNT_ID, watchList)
            .accept(MediaType.APPLICATION_JSON)
            .bearerAuth(token.getAccessToken());

    final HttpResponse<Object> deleted = client.toBlocking().exchange(request);
    assertEquals(HttpStatus.OK, deleted.getStatus());
    assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
  }

  private BearerAccessRefreshToken givenMyUserLoggedIn() {
    final UsernamePasswordCredentials credentials =
        new UsernamePasswordCredentials("my-user", "secret");
    var login = HttpRequest.POST("/login", credentials);
    var response = client.toBlocking().exchange(login, BearerAccessRefreshToken.class);
    assertEquals(HttpStatus.OK, response.getStatus());
    final BearerAccessRefreshToken token = response.body();
    assertNotNull(token);
    assertEquals("my-user", token.getUsername());
    return token;
  }
}
