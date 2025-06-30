package me.itzg.app;

import me.itzg.app.db.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collector;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class TrySpringDataJpaLiquibaseDecimalApplicationTests {

    @Autowired
    private AccountService accountService;

    @Test
    void contextLoads() {
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 10})
    void concurrentProblems(int nThreads) {
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
                        accountService.deposit(account.getId(), amount);
                    });
                }
            }

            System.out.println("waiting for all to complete");
        }

        BigDecimal expectedBalance = BigDecimal.ZERO;
        for (BigDecimal amount : amounts) {
            expectedBalance = expectedBalance.add(amount.multiply(BigDecimal.valueOf(repetitions)));
        }

        System.out.println("expected balance is " + expectedBalance);

        System.out.println("getting account");
        final Account result = accountService.get(account.getId());
        assertThat(result.getBalance()).isEqualTo(expectedBalance);

    }
}
