package it.auties.whatsapp.model.action;

import static it.auties.protobuf.base.ProtobufType.MESSAGE;

import it.auties.protobuf.base.ProtobufProperty;
import it.auties.whatsapp.model.sync.RecentEmojiWeight;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

/**
 * A model clas that represents a change in the weight of recent emojis
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder(access = AccessLevel.PROTECTED)
@Jacksonized
@Accessors(fluent = true)
public final class RecentEmojiWeightsAction
    implements Action {

  /**
   * The weight of the emojis
   */
  @ProtobufProperty(index = 1, type = MESSAGE, implementation = RecentEmojiWeight.class, repeated = true)
  private List<RecentEmojiWeight> weights;

  /**
   * Always throws an exception as this action cannot be serialized
   *
   * @return an exception
   */
  @Override
  public String indexName() {
    throw new UnsupportedOperationException("Cannot send action: no index name");
  }
}
