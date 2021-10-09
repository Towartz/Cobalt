package it.auties.whatsapp4j.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.auties.whatsapp4j.common.protobuf.model.media.MediaConnection;
import it.auties.whatsapp4j.common.response.JsonResponseModel;
import lombok.NonNull;

/**
 * A json model that contains a {@link MediaConnection}, used to decrypt media files
 *
 * @param status     the http status code for the original request
 * @param connection an instance of the requested connection
 */
public final record MediaConnectionResponse(int status,
                                            @NonNull @JsonProperty("media_conn") MediaConnection connection) implements JsonResponseModel {
}