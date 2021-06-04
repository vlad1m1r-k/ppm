package com.vladimir.ppm.service;

import com.vladimir.ppm.dto.CryptoDto;
import com.vladimir.ppm.provider.CryptoProviderImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CryptoProviderImpl.class)
public class CryptoProviderImplTest {
    @Autowired
    private CryptoProviderImpl cpi;
    private List<CryptoDto> list = new CopyOnWriteArrayList();

    @Ignore
    @Test
    public void testMultiThreadEncryption() throws InterruptedException, ExecutionException {
        ExecutorService service = Executors.newFixedThreadPool(50);
        List<EncryptWorker> encryptWorkers = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            encryptWorkers.add(new EncryptWorker(i));
        }
        List<Future<Void>> futures = service.invokeAll(encryptWorkers);
        for (Future<Void> future : futures) {
            future.get();
        }
        service.shutdown();
//        service.awaitTermination(10000L, TimeUnit.MILLISECONDS);
    }

    private class EncryptWorker implements Callable<Void> {
        private final SecureRandom random = new SecureRandom();
        private final int number;

        public EncryptWorker(int number) {
            this.number = number;
        }

        @Override
        public Void call() {
            String publicKey = "-----BEGIN PUBLIC KEY-----\r\n" +
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsGPbpLkD/wattp0U5GQF\r\n" +
                    "PBCy458XWcQnuqLTIcwLII2qsQ4XXoMu1HSq8KOhXn7K0IKOLcpkTMSrl9yTcKKD\r\n" +
                    "yGbH+UyO1u0YfRFbUSJwAb8zdDFrajC0PihP8hc0WUxpmoVr61/XdnZheJzOypfB\r\n" +
                    "juNY5HoAT7ZDXBPPRGD1yO0TWC5ZbYg2kIIupp6cgpZ9V768Mln7/HKSjenvwwlY\r\n" +
                    "9gLFwbGgPnvuoU/2StQSRet8qBJaaDja3x8xYs+nHz0PLmHwRZxEt1ruTXukDkU1\r\n" +
                    "MFg/T9QXV+FKoTwSYvdMghhDKxYabzsWE+4bOU0RkHe0POLt15W2OtbA8CTEukY3\r\n" +
                    "uwIDAQAB\r\n" +
                    "-----END PUBLIC KEY-----";
            for (int i = 0; i < 10; i++) {
                byte[] rndBytes = new byte[32];
                random.nextBytes(rndBytes);
                String str = new String(rndBytes);
//                System.out.println("Worker #" + number + " try " + i);
                try {
                    list.add(cpi.encrypt(publicKey, str));
                } catch (Exception e) {
                    System.out.println("EXCEPTION in Worker #" + number + " try " + i);
//                    e.printStackTrace();
                }
//                throw new IllegalArgumentException();
            }
            return null;
        }
    }
}