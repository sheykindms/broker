package com.sheykindms.broker.persistence.model;

import io.micronaut.core.annotation.Introspected;
import lombok.Data;

import java.math.BigDecimal;

@Introspected
@Data
public class QuoteDTO {
  private Integer id;
  private BigDecimal volume;
}
