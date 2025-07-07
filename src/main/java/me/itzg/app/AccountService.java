package me.itzg.app;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import me.itzg.app.db.Account;
import me.itzg.app.db.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
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
    public Account deposit(long id, BigDecimal amount, boolean useLocking) {
        final Account account;
        if (useLocking) {
            // this one uses @Lock
            account = accountRepository.findAccountById(id).orElseThrow();
        }
        else {
            // standard CrudRepository findById doesn't include the @Lock
            account = accountRepository.findById(id).orElseThrow();
        }
        account.setBalance(
            account.getBalance()
                .add(amount)
        );
        return accountRepository.save(account);
    }
}
