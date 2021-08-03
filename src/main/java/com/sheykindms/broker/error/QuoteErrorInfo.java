package com.sheykindms.broker.error;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Jacksonized
public class QuoteErrorInfo {
  private int status;
  private String error;
  private String message;
  private String path;
}
