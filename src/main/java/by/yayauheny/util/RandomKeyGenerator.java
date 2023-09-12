package by.yayauheny.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.security.DrbgParameters;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.security.DrbgParameters.Capability.PR_AND_RESEED;

@UtilityClass
public class RandomKeyGenerator {

    private final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private final SecureRandom random;

    static {
        Security.setProperty("securerandom.drbg.config", "Hash_DRBG, SHA-512");
        try {
            random = SecureRandom.getInstance(
                    "DRBG",
                    DrbgParameters.instantiation(256, PR_AND_RESEED, null)
            );
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateKey() {
        return IntStream.range(0, 7)
                .mapToObj(i -> IntStream.range(0, 4)
                        .mapToObj(j -> chars.charAt(random.nextInt(chars.length())))
                        .map(String::valueOf)
                        .collect(Collectors.joining()))
                .collect(Collectors.joining(" "));
    }
}
