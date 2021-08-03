package com.sheykindms.broker.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(name = "Quote", description = "quote data for selected stocks")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Quote {
  @Schema(description = "symbol")
  private Symbol symbol;

  @Schema(description = "highest price a buyer will pay for a security")
  private BigDecimal bid;

  @Schema(description = "lowest price a seller will accept for a security")
  private BigDecimal ask;

  @Schema(description = "last traded price for the security")
  private BigDecimal lastPrice;

  @Schema(description = "number of shares traded")
  private BigDecimal volume;
}
