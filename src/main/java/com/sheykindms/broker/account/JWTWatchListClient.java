package com.sheykindms.broker.account;

import com.sheykindms.broker.model.WatchList;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.reactivex.Flowable;

import java.util.UUID;

@Client("/")
public interface JWTWatchListClient {

  @Post("/login")
  BearerAccessRefreshToken login(@Body UsernamePasswordCredentials credentials);

  @Get("/account/watchlist-reactive/{accountId}")
  Flowable<WatchList> retrieveWatchList(@Header String authorization, UUID accountId);

  @Put("/account/watchlist-reactive/{accountId}")
  HttpResponse<WatchList> updateWatchList(
      @Header String authorization, @Body WatchList watchList, UUID accountId);

  @Delete("/account/watchlist-reactive/{accountId}")
  HttpResponse<WatchList> deleteWatchList(
      @Header String authorization, @PathVariable UUID accountId);
}
