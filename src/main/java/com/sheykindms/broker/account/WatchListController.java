package com.sheykindms.broker.account;

import com.sheykindms.broker.model.WatchList;
import com.sheykindms.broker.inmemorystore.InMemoryAccountStore;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/account/watchlist")
public class WatchListController {
  private static final Logger LOG = LoggerFactory.getLogger(WatchListController.class);
  private final InMemoryAccountStore store;

  public WatchListController(InMemoryAccountStore store) {
    this.store = store;
  }

  @Operation(summary = "Get the watchlist for given user")
  @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON))
  @Tag(name = "watchlist")
  @Get(value = "/{accountId}", produces = MediaType.APPLICATION_JSON)
  public WatchList get(@PathVariable UUID accountId) {
    LOG.debug("getWatchList - {}", Thread.currentThread().getName());
    return store.getWatchList(accountId);
  }

  @Operation(summary = "Update the watchlist for given user")
  @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON))
  @Tag(name = "watchlist")
  @Put(
      value = "/{accountId}",
      produces = MediaType.APPLICATION_JSON,
      consumes = MediaType.APPLICATION_JSON)
  public WatchList update(@Body WatchList watchList, @PathVariable UUID accountId) {
    return store.updateWatchList(accountId, watchList);
  }

  @Operation(summary = "Delete the watchlist for given user")
  @Tag(name = "watchlist")
  @Delete(
      value = "/{accountId}",
      produces = MediaType.APPLICATION_JSON,
      consumes = MediaType.APPLICATION_JSON)
  public void delete(@PathVariable UUID accountId) {
    store.deleteWatchList(accountId);
  }
}
