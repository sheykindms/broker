package com.sheykindms.broker.persistence.jpa;

import com.sheykindms.broker.persistence.model.QuoteDTO;
import com.sheykindms.broker.persistence.model.QuoteEntity;
import com.sheykindms.broker.persistence.model.SymbolEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuotesRepository extends CrudRepository<QuoteEntity, Integer> {

  Optional<QuoteEntity> findBySymbol(SymbolEntity entity);

  List<QuoteDTO> listOrderByVolumeDesc();

  List<QuoteDTO> listOrderByVolumeAsc();

  List<QuoteDTO> findByVolumeGreaterThanOrderByVolumeAsc(BigDecimal volume);
}
