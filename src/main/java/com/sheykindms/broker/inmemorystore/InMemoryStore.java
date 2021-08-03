package com.sheykindms.broker.inmemorystore;

import com.sheykindms.broker.model.Quote;
import com.sheykindms.broker.model.Symbol;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class InMemoryStore {
  private final List<Symbol> symbols;
  private final HashMap<String, Quote> cachedQuotes = new HashMap<>();
  private ThreadLocalRandom current = ThreadLocalRandom.current();

  public InMemoryStore() {
    symbols =
        Stream.of("AAPL", "AMZN", "FB", "GOOG", "MSFT", "NFLX", "TSLA")
            .map(Symbol::new)
            .collect(Collectors.toList());
    symbols.forEach(symbol -> cachedQuotes.put(symbol.getValue(), randomQuote(symbol)));
  }

  public List<Symbol> getAllSymbols() {
    return symbols;
  }

  public Optional<Quote> fetchQuote(final String symbol) {
    return Optional.ofNullable(cachedQuotes.get(symbol));
  }

  private Quote randomQuote(final Symbol symbol) {
    return Quote.builder()
        .symbol(symbol)
        .bid(randomValue())
        .ask(randomValue())
        .volume(randomValue())
        .build();
  }

  private BigDecimal randomValue() {
    return BigDecimal.valueOf(current.nextDouble(1, 100));
  }

  public Quote update(final Quote quote) {
    cachedQuotes.put(quote.getSymbol().getValue(), quote);
    return quote;
  }
}
