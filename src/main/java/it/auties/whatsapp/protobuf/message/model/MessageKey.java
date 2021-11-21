package it.auties.whatsapp.protobuf.message.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.protobuf.chat.Chat;
import it.auties.whatsapp.protobuf.info.MessageInfo;
import it.auties.whatsapp.manager.WhatsappStore;
import it.auties.whatsapp.utils.WhatsappUtils;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Optional;

/**
 * A container for unique identifiers and metadata linked to a {@link Message} and contained in {@link MessageInfo}.
 * This class is only a model, this means that changing its values will have no real effect on WhatsappWeb's servers.
 * Instead, methods inside {@link Whatsapp} should be used.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(builderMethodName = "newMessageKey", buildMethodName = "create")
@Accessors(fluent = true)
public class MessageKey {
  /**
   * The id of the message
   */
  @JsonProperty(value = "3")
  @Builder.Default
  private String id = WhatsappUtils.randomId();

  /**
   * The jid of the contact or group that sent the message.
   */
  @JsonProperty(value = "1")
  private String chatJid;

  /**
   * Determines whether the message was sent by you or by someone else
   */
  @JsonProperty(value = "2")
  private boolean fromMe;

  /**
   * Constructs a MessageKey for a message with a random id, sent by someone that isn't yourself and a provided chat
   *
   * @param chat the message's chat
   */
  public MessageKey(@NonNull Chat chat) {
    this(WhatsappUtils.randomId(), chat.jid(), true);
  }

  /**
   * Returns the chat where the message was sent
   *
   * @return an optional wrapping a {@link Chat}
   */
  public Optional<Chat> chat(){
    return WhatsappStore.singletonInstance().findChatByJid(chatJid);
  }
}