package it.auties.whatsapp.protobuf.signal.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.auties.whatsapp.util.BytesDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Accessors(fluent = true)
public class ServerCertificate {
  @JsonProperty("1")
  @JsonPropertyDescription("bytes")
  @JsonDeserialize(using = BytesDeserializer.class)
  private byte[] details;

  @JsonProperty("2")
  @JsonPropertyDescription("bytes")
  @JsonDeserialize(using = BytesDeserializer.class)
  private byte[] signature;
}
