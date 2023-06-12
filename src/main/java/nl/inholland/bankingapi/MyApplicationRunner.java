package nl.inholland.bankingapi;

import jakarta.transaction.Transactional;
import nl.inholland.bankingapi.model.*;
import nl.inholland.bankingapi.repository.AccountRepository;
import nl.inholland.bankingapi.repository.TransactionRepository;
import nl.inholland.bankingapi.repository.UserRepository;
import nl.inholland.bankingapi.service.AccountService;
import nl.inholland.bankingapi.service.TransactionService;
import nl.inholland.bankingapi.service.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyApplicationRunner implements ApplicationRunner {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final UserService userService;
    private final AccountService accountService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public MyApplicationRunner(TransactionRepository transactionRepository, AccountRepository accountRepository, UserRepository userRepository, TransactionService transactionService, UserService userService, AccountService accountService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.transactionService = transactionService;
        this.userService = userService;
        this.accountService = accountService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        loadInformationForDB();
    }

    public void loadInformationForDB() {
        //Load users
        User employee = new User("employee@email.com", bCryptPasswordEncoder.encode("1234"), "User2", "User", "11-11-2000",
                "123456789", "Street", "1234AB", "City", UserType.ROLE_EMPLOYEE, 500.00, 10000.00, true);

        User user1 = new User("user@email.com", bCryptPasswordEncoder.encode("1234"), "User1", "User", "11-11-2000",
                "123456789", "Street", "1234AB", "City", UserType.ROLE_CUSTOMER, 1000.00, 1000.00, true);
        User employee2 = new User("employee2@email.com", bCryptPasswordEncoder.encode("1234"), "User2", "User", "11-11-2000",

                "123456789", "Street", "1234AB", "City", UserType.ROLE_EMPLOYEE, 10000.00, 10000.00, true);

        User customer = new User("customer@email.com", bCryptPasswordEncoder.encode("1234"), "Customer", "Customer", "11-11-2000",
                "123456789", "Street", "1234AB", "City", UserType.ROLE_CUSTOMER, 10000.00, 10000.00, true);

        //Load Accounts
        Account account1 = new Account(customer, "NL21INHO0123400081", 900.00, 0.00, AccountType.CURRENT, true);
        Account closedAccount= new Account(customer, "NL21INHO0123400085", 90000.00, 0.00, AccountType.CURRENT, false);
        Account savings = new Account(customer, "NL21INHO0123400083", 9000.00, 0.00, AccountType.SAVINGS, true);
        Account account2 = new Account(employee, "NL21INHO0123400082", 9000.00, 0.00, AccountType.CURRENT, true);
        Account savings2 = new Account(employee, "NL21INHO0123400084", 9000.00, 0.00, AccountType.SAVINGS, true);

        Account bank = new Account(employee, "NL01INHO0000000001", 9000.00, 0.00, AccountType.BANK, true);
        Account savings3 = new Account(employee, "NL01INHO0000000002", 9000.00, 0.00, AccountType.SAVINGS, true);

        Transaction transaction = new Transaction(account1, account2, 100.00, LocalDateTime.now(), TransactionType.TRANSFER, customer);
        Transaction transaction2 = new Transaction(savings, savings2, 100.00, LocalDateTime.now(), TransactionType.TRANSFER, employee);

        //Save to DB
        transactionRepository.save(transaction);
        transactionRepository.save(transaction2);
        accountRepository.save(account1);
        accountRepository.save(account2);
        accountRepository.save(savings);
        accountRepository.save(closedAccount);
        accountRepository.save(savings2);
        accountRepository.save(savings3);
        accountRepository.save(bank);
        userRepository.save(employee);
        userRepository.save(user1);
        userRepository.save(employee2);
        userRepository.save(customer);
    }

}