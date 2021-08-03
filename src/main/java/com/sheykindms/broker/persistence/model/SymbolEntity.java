package com.sheykindms.broker.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "symbol")
@Table(name = "symbols", schema = "br")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SymbolEntity {
  @Id
  private String value;

  //Some lombok and micronaut compatibility issues :-)
  public String getValue() {
    return value;
  }
}
