package com.sheykindms.broker.persistence.jpa;

import com.sheykindms.broker.persistence.model.SymbolEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface SymbolsRepository extends CrudRepository<SymbolEntity, String> {}
