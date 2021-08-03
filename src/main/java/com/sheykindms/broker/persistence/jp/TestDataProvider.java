package com.sheykindms.broker.persistence.jp;

import com.sheykindms.broker.persistence.jpa.QuotesRepository;
import com.sheykindms.broker.persistence.model.QuoteEntity;
import com.sheykindms.broker.persistence.model.SymbolEntity;
import com.sheykindms.broker.persistence.jpa.SymbolsRepository;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/** Inserts mock data into the DB */
@Singleton
public class TestDataProvider {
  private static final Logger LOG = LoggerFactory.getLogger(TestDataProvider.class);
  private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
  private final SymbolsRepository symbols;
  private final QuotesRepository quotes;

  public TestDataProvider(SymbolsRepository symbols, QuotesRepository quotes) {
    this.symbols = symbols;
    this.quotes = quotes;
  }

  @EventListener
  public void init(StartupEvent event) {
    List<SymbolEntity> symbolEntities = (List<SymbolEntity>) symbols.findAll();
    if (symbolEntities.isEmpty()) {
      LOG.info("Adding mock data to symbols relation");
      Stream.of("AAPL", "AMZN", "FB", "TSLA").map(SymbolEntity::new).forEach(symbols::save);
    }
    List<QuoteEntity> quoteEntities = (List<QuoteEntity>) quotes.findAll();
    if (quoteEntities.isEmpty()) {
      LOG.info("Adding mock data to quotes relation");
      symbols.findAll().forEach(
          symbol -> {
            var quote = new QuoteEntity();
            quote.setSymbol(symbol);
            quote.setAsk(randomValue());
            quote.setBid(randomValue());
            quote.setLastPrice(randomValue());
            quote.setVolume(randomValue());
            quotes.save(quote);
          });
    }
  }

  private BigDecimal randomValue() {
    return BigDecimal.valueOf(RANDOM.nextDouble(1, 100));
  }
}
