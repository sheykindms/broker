package com.sheykindms.broker.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Schema(name = "WatchList", description = "list of securities monitored by the user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatchList {
  @Schema(description = "securities monitored by the user")
  private List<Symbol> symbols = new ArrayList<>();
}
