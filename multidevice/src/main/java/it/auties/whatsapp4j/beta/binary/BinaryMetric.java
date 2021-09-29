package it.auties.whatsapp4j.beta.binary;

import it.auties.whatsapp4j.common.binary.BinaryArray;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.stream.IntStream;


@AllArgsConstructor
@Accessors(fluent = true)
public enum BinaryMetric {
    DEBUG_LOG(1),
    QUERY_RESUME(2),
    LIVE_LOCATION(3),
    QUERY_MEDIA(4),
    QUERY_CHAT(5),
    QUERY_CONTACT(6),
    QUERY_MESSAGES(7),
    PRESENCE(8),
    PRESENCE_SUBSCRIBE(9),
    GROUP(10),
    READ(11),
    CHAT(12),
    RECEIVED(13),
    PICTURE(14),
    STATUS(15),
    MESSAGE(16),
    QUERY_ACTIONS(17),
    BLOCK(18),
    QUERY_GROUP(19),
    QUERY_PREVIEW(20),
    QUERY_EMOJI(21),
    QUERY_VCARD(29),
    QUERY_STATUS(30),
    QUERY_STATUS_UPDATE(31),
    QUERY_LIVE_LOCATION(33),
    QUERY_LABEL(36),
    QUERY_QUICK_REPLY(39);

    @Getter
    private final int data;

    /**
     * Converts {@code tags} to an array of bytes using the data that they wrap
     *
     * @param tags the tags to convert
     * @return a new array of bytes
     */
    public static @NonNull BinaryArray toArray(@NonNull BinaryMetric... tags){
        var data = new byte[tags.length];
        IntStream.range(0, tags.length).forEach(index -> data[index] = (byte) tags[index].data());
        return BinaryArray.forArray(data);
    }
}
