package com.sheykindms.broker.account;

import com.sheykindms.broker.model.WatchList;
import com.sheykindms.broker.inmemorystore.InMemoryAccountStore;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
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
@Controller("/account/watchlist-reactive")
public class WatchListControllerReactive {
  private static final Logger LOG = LoggerFactory.getLogger(WatchListControllerReactive.class);
  private final InMemoryAccountStore store;

  public WatchListControllerReactive(InMemoryAccountStore store) {
    this.store = store;
  }

  @Operation(summary = "Get the watchlist for given user")
  @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON))
  @Tag(name = "watchlist-reactive")
  @Get(value = "/{accountId}", produces = MediaType.APPLICATION_JSON)
  @ExecuteOn(TaskExecutors.IO)
  public WatchList get(@PathVariable UUID accountId) {
    LOG.debug("getWatchListReactive - {}", Thread.currentThread().getName());
    return store.getWatchList(accountId);
  }

  @Operation(summary = "Update the watchlist for given user")
  @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON))
  @Tag(name = "watchlist")
  @Put(
      value = "/{accountId}",
      produces = MediaType.APPLICATION_JSON,
      consumes = MediaType.APPLICATION_JSON)
  @ExecuteOn(TaskExecutors.IO)
  public WatchList update(@PathVariable UUID accountId, @Body WatchList watchList) {
    return store.updateWatchList(accountId, watchList);
  }

  @Operation(summary = "Delete the watchlist for given user")
  @Tag(name = "watchlist")
  @Delete(
      value = "{accountId}",
      produces = MediaType.APPLICATION_JSON,
      consumes = MediaType.APPLICATION_JSON)
  @ExecuteOn(TaskExecutors.IO)
  public void delete(@PathVariable UUID accountId) {
    store.deleteWatchList(accountId);
  }
}
