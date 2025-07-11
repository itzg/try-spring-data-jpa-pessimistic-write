package me.itzg.app;

import me.itzg.app.db.Account;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ApplicationTests {

    @Autowired
    private AccountService accountService;

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 10})
    void concurrencyIsFineWithLocking(int nThreads) {
        runDepositsTest(nThreads, true);
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 10})
    void concurrencyBreaksWithoutLocking(int nThreads) {
        runDepositsTest(nThreads, false);
    }

    private void runDepositsTest(int nThreads, boolean useLocking) {
        final Account account = accountService.create();

        final int repetitions = 100;
        final List<BigDecimal> amounts = List.of(
            BigDecimal.valueOf(12.25),
            BigDecimal.valueOf(5.10),
            BigDecimal.valueOf(19.99)
        );

        try (ExecutorService executorService = Executors.newFixedThreadPool(nThreads)) {
            System.out.println("submitting " + repetitions + "x" + amounts.size() + " deposits");
            for (int i = 0; i < repetitions; i++) {
                for (BigDecimal amount : amounts) {
                    executorService.submit(() -> {
                        accountService.deposit(account.getId(), amount, useLocking);
                    });
                }
            }

            System.out.println("waiting for all to complete");
        }

        BigDecimal expectedBalance = BigDecimal.ZERO;
        for (BigDecimal amount : amounts) {
            expectedBalance = expectedBalance.add(amount.multiply(BigDecimal.valueOf(repetitions)));
        }

        final Account result = accountService.get(account.getId());
        assertThat(result.getBalance()).isEqualTo(expectedBalance);
    }
}
