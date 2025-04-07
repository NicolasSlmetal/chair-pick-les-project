package com.chairpick.ecommerce.exceptions;

public class AmountExceedsException extends RuntimeException {
  public AmountExceedsException(String message) {
    super(message);
  }
}
