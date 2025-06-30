package me.itzg.app;

import jakarta.transaction.Transactional;
import me.itzg.app.db.Account;
import me.itzg.app.db.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account create() {
        return accountRepository.save(
            new Account()
            .setBalance(BigDecimal.ZERO)
        );
    }

    public Account get(long id) {
        return accountRepository.findById(id).orElseThrow();
    }

    @Transactional
    public Account deposit(long id, BigDecimal amount) {
        final Account account = accountRepository.findAccountById(id).orElseThrow();
        account.setBalance(account.getBalance().add(amount));
        return accountRepository.save(account);
    }
}
