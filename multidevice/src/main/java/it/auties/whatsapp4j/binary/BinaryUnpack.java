package it.auties.whatsapp4j.binary;

import it.auties.whatsapp4j.common.binary.BinaryBuffer;
import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.Inflater;

@UtilityClass
public class BinaryUnpack {
    private int counter;
    private String uuid;

    public BinaryBuffer unpack(byte[] input) {
        var data = BinaryBuffer.fromBytes(input);
        var token = data.readUInt8() & 2;
        if (token == 0) {
            return data.remaining();
        }

        try {
            var decompressor = new Inflater();
            decompressor.setInput(data.remaining().readAllBytes());
            var temp = new byte[2048];
            var length = decompressor.inflate(temp);
            var result = new byte[length];
            System.arraycopy(temp, 0, result, 0, length);
            return BinaryBuffer.fromBytes(result);
        }catch (Exception exception){
            throw new RuntimeException("Cannot inflate data", exception);
        }
    }

    public String generateId() {
        return "%s-%s".formatted(Objects.requireNonNullElseGet(uuid, BinaryUnpack::createUUID), counter++);
    }

    private String createUUID() {
        var bytes = new byte[2];
        ThreadLocalRandom.current().nextBytes(bytes);
        return (uuid = "%s.%s".formatted(Byte.toUnsignedInt(bytes[0]), Byte.toUnsignedInt(bytes[1])));
    }
}