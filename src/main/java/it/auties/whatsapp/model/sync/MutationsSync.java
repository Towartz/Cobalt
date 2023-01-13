package it.auties.whatsapp.model.sync;

import static it.auties.protobuf.base.ProtobufType.MESSAGE;

import it.auties.protobuf.base.ProtobufMessage;
import it.auties.protobuf.base.ProtobufName;
import it.auties.protobuf.base.ProtobufProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

@AllArgsConstructor
@Data
@Builder
@Jacksonized
@Accessors(fluent = true)
@ProtobufName("SyncdMutations")
public class MutationsSync
    implements ProtobufMessage {

  @ProtobufProperty(index = 1, type = MESSAGE, implementation = MutationSync.class, repeated = true)
  private List<MutationSync> mutations;
}