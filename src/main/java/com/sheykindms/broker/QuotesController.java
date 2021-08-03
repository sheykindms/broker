package com.sheykindms.broker;

import com.sheykindms.broker.error.QuoteErrorInfo;
import com.sheykindms.broker.model.Quote;
import com.sheykindms.broker.persistence.jpa.QuotesRepository;
import com.sheykindms.broker.persistence.model.QuoteDTO;
import com.sheykindms.broker.persistence.model.QuoteEntity;
import com.sheykindms.broker.persistence.model.SymbolEntity;
import com.sheykindms.broker.inmemorystore.InMemoryStore;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/quotes")
public class QuotesController {
  private final InMemoryStore store;
  private final QuotesRepository quotes;

  public QuotesController(InMemoryStore store, QuotesRepository quotes) {
    this.store = store;
    this.quotes = quotes;
  }

  @Operation(summary = "Returns a quote for the given symbol")
  @ApiResponse(responseCode = "400", description = "Invalid symbol specified")
  @Tag(name = "quotes")
  @Get("/{symbol}")
  public HttpResponse getQuote(@PathVariable String symbol) {
    Optional<Quote> maybeQuote = store.fetchQuote(symbol);
    if (maybeQuote.isEmpty()) {
      QuoteErrorInfo notFound =
          QuoteErrorInfo.builder()
              .status(HttpStatus.NOT_FOUND.getCode())
              .error(HttpStatus.NOT_FOUND.name())
              .message("quote for the symbol is not available")
              .path("/quotes/" + symbol)
              .build();
      return HttpResponse.notFound(notFound);
    }
    return HttpResponse.ok(maybeQuote.get());
  }

  @Operation(summary = "Returns a quote for the given symbol from database")
  @ApiResponse(responseCode = "400", description = "Invalid symbol specified")
  @Tag(name = "quotes")
  @Get("/jpa/{symbol}")
  public HttpResponse getQuotesViaJpa(@PathVariable String symbol) {
    final Optional<QuoteEntity> maybeQuote = quotes.findBySymbol(new SymbolEntity(symbol));
    if (maybeQuote.isEmpty()) {
      QuoteErrorInfo notFound =
          QuoteErrorInfo.builder()
              .status(HttpStatus.NOT_FOUND.getCode())
              .error(HttpStatus.NOT_FOUND.name())
              .message("quote for the symbol is not available in database")
              .path("/quotes/" + symbol)
              .build();
      return HttpResponse.notFound(notFound);
    }
    return HttpResponse.ok(maybeQuote.get());
  }

  @Operation(summary = "Returns a quote for the given symbol")
  @Tag(name = "quotes")
  @Get("/jpa")
  public Iterable<QuoteEntity> getAllQuotesViaJpa() {
    return quotes.findAll();
  }

  @Operation(summary = "Returns a list of volumes in descending order from database")
  @Tag(name = "quotes")
  @Get("/jpa/ordered/desc")
  public List<QuoteDTO> orderedDesc() {
    return quotes.listOrderByVolumeDesc();
  }

  @Operation(summary = "Returns a list of volumes in ascending order from database")
  @Tag(name = "quotes")
  @Get("/jpa/ordered/asc")
  public List<QuoteDTO> orderedAsc() {
    return quotes.listOrderByVolumeAsc();
  }

  @Operation(summary = "Returns volumes from database greater than given value in ascending order")
  @Tag(name = "quotes")
  @Get("/jpa/volume/{volume}")
  public List<QuoteDTO> volumeFilter(@PathVariable BigDecimal volume) {
    return quotes.findByVolumeGreaterThanOrderByVolumeAsc(volume);
  }
}
