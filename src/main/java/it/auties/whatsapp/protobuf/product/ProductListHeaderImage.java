package it.auties.whatsapp.protobuf.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Jacksonized
@Builder
@Accessors(fluent = true)
public class ProductListHeaderImage {
  @JsonProperty("1")
  @JsonPropertyDescription("string")
  private String productId;

  @JsonProperty("2")
  @JsonPropertyDescription("bytes")
  private byte[] thumbnail;
}
