package me.itzg.app;

import me.itzg.app.db.Account;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public Account create() {
        return accountService.create();
    }

    @GetMapping("{id}")
    public Account get(@PathVariable long id) {
        return accountService.get(id);
    }

    @PostMapping("{id}/_deposit")
    public Account deposit(@PathVariable long id, @RequestParam BigDecimal amount) {
        return accountService.deposit(id, amount);
    }
}
