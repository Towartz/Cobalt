package it.auties.whatsapp.crypto;

import it.auties.whatsapp.manager.WhatsappKeys;
import it.auties.whatsapp.protobuf.signal.keypair.SignalKeyPair;
import it.auties.whatsapp.protobuf.signal.message.SignalDistributionMessage;
import it.auties.whatsapp.protobuf.signal.sender.*;
import it.auties.whatsapp.util.Validate;
import lombok.NonNull;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.NoSuchElementException;

public record SignalGroup(SenderKeyName senderKeyId, @NonNull WhatsappKeys keys) {
    private static final int MAX_MESSAGE_KEYS = 2000;
    private static final String AES_CBC = "AES/CBC/PKCS5Padding";
    private static final String AES = "AES";
    public SignalGroup(WhatsappKeys keys){
        this(null, keys);
    }

    public SignalDistributionMessage create(SenderKeyName senderKeyName) {
        var senderKeyRecord = keys.findSenderKeyByName(senderKeyName)
                .orElseThrow(() -> new NoSuchElementException("Cannot find sender key for name %s".formatted(senderKeyName)));
        if (senderKeyRecord.isEmpty()) {
            senderKeyRecord.senderKeyState(SignalHelper.randomSenderKeyId(), 0, SignalHelper.randomSenderKey(), SignalKeyPair.random().privateKey());
            keys.senderKeyStructures().add(new SenderKeyStructure(senderKeyName, senderKeyRecord));
        }

        var state = senderKeyRecord.senderKeyState();
        var chainKey = state.senderChainKey();
        return new SignalDistributionMessage(state.senderKeyId(), chainKey.iteration(), chainKey.seed(), state.signingKeyPublic());
    }

    public void process(SenderKeyName senderKeyName, SignalDistributionMessage senderKeyDistributionMessage) {
        var senderKeyRecord = keys.findSenderKeyByName(senderKeyName)
                .orElseThrow(() -> new NoSuchElementException("Cannot find sender key for name %s".formatted(senderKeyName)));
        senderKeyRecord.addSenderKeyState(senderKeyDistributionMessage.id(), senderKeyDistributionMessage.iteration(), senderKeyDistributionMessage.chainKey(), senderKeyDistributionMessage.signingKey());
        keys.senderKeyStructures().add(new SenderKeyStructure(senderKeyName, senderKeyRecord));
    }

    public byte[] cipher(byte[] paddedPlaintext) {
        var record = keys.findSenderKeyByName(senderKeyId)
                .orElseThrow(() -> new NoSuchElementException("Cannot find sender key for name %s".formatted(senderKeyId)));
        var senderKeyState = record.senderKeyState();
        var senderKey = senderKeyState.senderChainKey().toSenderMessageKey();
        var ciphertext = cipherText(senderKey.iv(), senderKey.cipherKey(), paddedPlaintext);
        var senderKeyMessage = new SenderKeyMessage(senderKeyState.senderKeyId(), senderKey.iteration(), ciphertext, senderKeyState.signingKeyPrivate());
        senderKeyState.senderChainKey(senderKeyState.senderChainKey().next());
        keys.senderKeyStructures().add(new SenderKeyStructure(senderKeyId, record));
        return senderKeyMessage.serialized();
    }

    public byte[] decipher(byte[] senderKeyMessageBytes) {
        var record = keys.findSenderKeyByName(senderKeyId)
                .orElseThrow(() -> new NoSuchElementException("Cannot find sender key for name %s".formatted(senderKeyId)));
        Validate.isTrue(!record.isEmpty(), "No sender key for %s", senderKeyId);
        var senderKeyMessage = new SenderKeyMessage(senderKeyMessageBytes);
        var senderKeyState = record.senderKeyState(senderKeyMessage.id());
        senderKeyMessage.verifySignature(senderKeyState.signingKeyPublic());
        var senderKey = senderKey(senderKeyState, senderKeyMessage.iteration());
        var plaintext = plainText(senderKey.iv(), senderKey.cipherKey(), senderKeyMessage.cipherText());
        keys.senderKeyStructures().add(new SenderKeyStructure(senderKeyId, record));
        return plaintext;
    }

    private SenderMessageKey senderKey(SenderKeyState senderKeyState, int iteration) {
        var senderChainKey = senderKeyState.senderChainKey();
        if (senderChainKey.iteration() > iteration) {
            Validate.isTrue(senderKeyState.hasSenderMessageKey(iteration),
                    "Received message with old counter: %s,%s",
                    senderChainKey.iteration(), iteration);
            return senderKeyState.removeSenderMessageKey(iteration);
        }

        Validate.isTrue(iteration - senderChainKey.iteration() <= MAX_MESSAGE_KEYS,
                "Message overflow: %s messages over the %s limit",
                MAX_MESSAGE_KEYS - iteration - senderChainKey.iteration(), MAX_MESSAGE_KEYS);
        while (senderChainKey.iteration() < iteration) {
            senderKeyState.addSenderMessageKey(senderChainKey.toSenderMessageKey());
            senderChainKey = senderChainKey.next();
        }

        senderKeyState.senderChainKey(senderChainKey.next());
        return senderChainKey.toSenderMessageKey();
    }

    @SneakyThrows
    private byte[] plainText(byte[] iv, byte[] key, byte[] ciphertext) {
        var ivParameterSpec = new IvParameterSpec(iv);
        var cipher = Cipher.getInstance(AES_CBC);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, AES), ivParameterSpec);
        return cipher.doFinal(ciphertext);
    }

    @SneakyThrows
    private byte[] cipherText(byte[] iv, byte[] key, byte[] plaintext) {
        var ivParameterSpec = new IvParameterSpec(iv);
        var cipher = Cipher.getInstance(AES_CBC);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, AES), ivParameterSpec);
        return cipher.doFinal(plaintext);
    }
}