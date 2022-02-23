package it.auties.whatsapp.crypto;

import it.auties.whatsapp.binary.BinaryArray;
import it.auties.whatsapp.manager.WhatsappKeys;
import lombok.NonNull;
import lombok.SneakyThrows;

import static it.auties.whatsapp.binary.BinaryArray.of;

public class Handshake {
    public static final byte[] PROLOGUE = new byte[]{87, 65, 5, 2};
    private static final BinaryArray PROTOCOL = BinaryArray.of("Noise_XX_25519_AESGCM_SHA256\0\0\0\0");

    private WhatsappKeys keys;
    private BinaryArray hash;
    private BinaryArray salt;
    private BinaryArray cryptoKey;
    private long counter;

    public void start(WhatsappKeys keys) {
        this.hash = PROTOCOL;
        this.salt = PROTOCOL;
        this.cryptoKey = PROTOCOL;
        this.keys = keys;
        this.counter = 0;
        updateHash(PROLOGUE);
    }

    @SneakyThrows
    public void updateHash(byte @NonNull [] data) {
        var input = hash.append(data);
        this.hash = Sha256.calculate(input.data());
    }

    @SneakyThrows
    public byte[] cipher(byte @NonNull [] bytes, boolean encrypt) {
        var cyphered = AesGmc.with(cryptoKey, hash.data(), counter++, encrypt)
                .process(bytes);
        if (!encrypt) {
            updateHash(bytes);
            return cyphered;
        }

        updateHash(cyphered);
        return cyphered;
    }

    public void finish() {
        var expanded = extractAndExpandWithHash(new byte[0]);
        keys.writeKey(expanded.cut(32))
                .readKey(expanded.slice(32))
                .save(true);
    }

    public void mixIntoKey(byte @NonNull [] bytes) {
        var expanded = extractAndExpandWithHash(bytes);
        this.salt = expanded.cut(32);
        this.cryptoKey = expanded.slice(32);
        this.counter = 0;
    }

    private BinaryArray extractAndExpandWithHash(byte @NonNull [] key) {
        var extracted = Hkdf.extract(salt.data(), key);
        var expanded = Hkdf.expand(extracted, null, 64);
        return of(expanded);
    }
}