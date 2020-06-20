package ru.zoommax;

import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;

public class Crypto {
    String salt = KeyGenerators.string().generateKey();
    MessageDigestPasswordEncoder enco = new MessageDigestPasswordEncoder("SHA-256");
    String encrypt(String data, String pass){
        TextEncryptor te = Encryptors.text(pass, salt);
        return te.encrypt(data);
    }
    String encodeSha(String data){
        return enco.encode(data);
    }
}
