package it.auties.whatsapp.protobuf.button;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import it.auties.whatsapp.protobuf.message.button.StructuredButtonMessage;
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
public class CallButton {
  @JsonProperty("1")
  @JsonPropertyDescription("HighlyStructuredMessage")
  private StructuredButtonMessage displayText;

  @JsonProperty("2")
  @JsonPropertyDescription("HighlyStructuredMessage")
  private StructuredButtonMessage phoneNumber;
}
