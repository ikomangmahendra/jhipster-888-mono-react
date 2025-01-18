package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProviderTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Provider getProviderSample1() {
        return new Provider().id(1L).provider("provider1").abn("abn1").contactPerson("contactPerson1");
    }

    public static Provider getProviderSample2() {
        return new Provider().id(2L).provider("provider2").abn("abn2").contactPerson("contactPerson2");
    }

    public static Provider getProviderRandomSampleGenerator() {
        return new Provider()
            .id(longCount.incrementAndGet())
            .provider(UUID.randomUUID().toString())
            .abn(UUID.randomUUID().toString())
            .contactPerson(UUID.randomUUID().toString());
    }
}
