package org.ws13.learning.sse.simpleweb4j.sse;

import java.util.UUID;

/**
 * @author ctranxuan
 */
public class Sse<T> {
  private String id;
  private String type;
  private T data;
  private int retry;

  public Sse(final String aType, final T aData) {
    this(aType, aData, 1500);
  }

  public Sse(final String aType, final T aData, final int aRetry) {
    this(UUID.randomUUID().toString(), aType, aData, aRetry);
  }

  public Sse(final String aId, final String aType, final T aData, final int aRetry) {
    id = aId;
    type = aType;
    data = aData;
    retry = aRetry;
  }

  public String getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  public T getData() {
    return data;
  }

  public int getRetry() {
    return retry;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Sse<?> sse = (Sse<?>) o;

    return !(id != null ? !id.equals(sse.id) : sse.id != null);

  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Sse{" +
        "id='" + id + '\'' +
        ", type='" + type + '\'' +
        ", data=" + data +
        ", retry=" + retry +
        '}';
  }
}
