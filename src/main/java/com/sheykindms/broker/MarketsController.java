package com.sheykindms.broker;

import com.sheykindms.broker.model.Symbol;
import com.sheykindms.broker.persistence.model.SymbolEntity;
import com.sheykindms.broker.persistence.jpa.SymbolsRepository;
import com.sheykindms.broker.inmemorystore.InMemoryStore;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.reactivex.Single;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/markets")
public class MarketsController {
  private final InMemoryStore store;
  private final SymbolsRepository symbols;

  public MarketsController(InMemoryStore store, SymbolsRepository symbols) {
    this.store = store;
    this.symbols = symbols;
  }

    @Operation(summary = "Return all available markets")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @Tag(name = "markets")
    @Get
    public List<Symbol> getSymbols() {
      return store.getAllSymbols();
    }

  @Operation(summary = "Return all available markets from database using JPA")
  @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON))
  @Tag(name = "markets")
  @Get("/jpa")
  public Single<Iterable<SymbolEntity>> getSymbolsViaJpa() {
    return Single.just(symbols.findAll());
  }
}
