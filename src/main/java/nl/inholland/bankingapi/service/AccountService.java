package nl.inholland.bankingapi.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import nl.inholland.bankingapi.exception.ApiRequestException;
import nl.inholland.bankingapi.filter.JwtTokenFilter;
import nl.inholland.bankingapi.jwt.JwtTokenProvider;
import nl.inholland.bankingapi.model.*;
import nl.inholland.bankingapi.model.dto.*;
import nl.inholland.bankingapi.model.specifications.AccountSpecifications;
import nl.inholland.bankingapi.model.specifications.TransactionSpecifications;
import nl.inholland.bankingapi.repository.AccountRepository;
import nl.inholland.bankingapi.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

//import javax.persistence.EntityNotFoundException;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class AccountService {

    private AccountRepository accountRepository;
    private UserRepository userRepository;
    private List<Account> accounts;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenFilter jwtTokenFilter;

    private final HttpServletRequest request;


    public AccountService(AccountRepository accountRepository, UserRepository userRepository, JwtTokenProvider jwtTokenProvider, JwtTokenFilter jwtTokenFilter, HttpServletRequest request) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtTokenFilter = jwtTokenFilter;
        this.request = request;
    }



    public void userHasAccount(User user) {
        user.setHasAccount(true);
        user.setUserType(UserType.ROLE_CUSTOMER);
        userRepository.save(user);
    }


    public Account disableAccount(@PathVariable long id, @RequestBody AccountPUT_DTO accountPUT_dto) {
        Account account = mapDtoToAccountPut(id, accountPUT_dto);
        account.getUser().setHasAccount(isUserHasNoActiveAccounts(account.getUser().getId()));
        userRepository.save(account.getUser());
        return accountRepository.save(account);
    }

    private Account mapDtoToAccountPut(long id, AccountPUT_DTO dto) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Account not found"));
        if (account.getBalance() < dto.absoluteLimit()) {
            throw new ApiRequestException("Absolute limit cannot be higher than account balance", HttpStatus.BAD_REQUEST);
        }
        if (dto.isActive() != null) {
            account.setIsActive(dto.isActive());
        }

        account.setAbsoluteLimit(dto.absoluteLimit());

        return account;
    }

    protected AccountGET_DTO accountGETDto(Account account) {
        return new AccountGET_DTO(
                account.getAccountId(),
                account.getUser().getId(),
                account.getIBAN(),
                account.getBalance(),
                account.getAbsoluteLimit(),
                account.getAccountType(),
                account.getIsActive()
        );
    }

    private AccountIbanGET_DTO accountIbanGET_DTO(Account account) {
        return new AccountIbanGET_DTO(
                account.getUser().getFirstName(),
                account.getUser().getLastName(),
                account.getIBAN()
        );
    }

    public List<AccountGET_DTO> getAllAccountsByUserId(long id) {
        List<AccountGET_DTO> accountsOwnedBySpecificUser = new ArrayList<>();
        for (Account account : accountRepository.getAllAccountsByUserId(id)) {
            accountsOwnedBySpecificUser.add(accountGETDto(account));
        }
        return accountsOwnedBySpecificUser;
    }

    public List<AccountGET_DTO> getAllAccounts(Integer offset, Integer limit, String firstName, String lastName, AccountType accountType, Double absoluteLimit, Boolean isActive, Long user) {
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Account> accountSpecification = AccountSpecifications.getSpecifications(firstName, lastName, accountType, absoluteLimit, isActive, user);

        List<AccountGET_DTO> userAccounts = new ArrayList<>();
        List<AccountGET_DTO> accounts = new ArrayList<>();
        for (Account account : accountRepository.findAll(accountSpecification, pageable)) {
            if(!account.getAccountType().equals(AccountType.BANK)){
                accounts.add(accountGETDto(account));
            }
            if (account.getUser().getId().equals(getLoggedInUser(request).getId())) {
                userAccounts.add(accountGETDto(account));
            }
        }
        if (getLoggedInUser(request).getUserType().equals(UserType.ROLE_CUSTOMER)) {
            return userAccounts;
        }
        return accounts;
    }

    public User getLoggedInUser(HttpServletRequest request) {
        // Get JWT token and the information of the authenticated user
        String receivedToken = jwtTokenFilter.getToken(request);
        jwtTokenProvider.validateToken(receivedToken);
        Authentication authenticatedUserUsername = jwtTokenProvider.getAuthentication(receivedToken);
        String userEmail = authenticatedUserUsername.getName();
        return userRepository.findUserByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));
    }

    public AccountGET_DTO getAccountById(long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        if(account.getAccountType() == AccountType.BANK){
            throw new ApiRequestException("Bank account cannot be accessed", HttpStatus.BAD_REQUEST);
        }
        return accountGETDto(account);
    }


    public List<AccountIbanGET_DTO> getIBANByUserFirstName(String firstName) {
        List<AccountIbanGET_DTO> accounts = new ArrayList<>();
        for (Account account : accountRepository.getIBANByUserFirstName(firstName)) {
            accounts.add(accountIbanGET_DTO(account));
        }
        return accounts;
    }

    public Account addAccount(AccountPOST_DTO account) {
//        if (!isCustomer(account.userId())) {
//            throw new ApiRequestException("Employee type cannot own accounts", HttpStatus.BAD_REQUEST);
//        }
        if (hasAccountOfType(account.userId(), account.accountType())) {
            throw new ApiRequestException("User already has an account of type " + account.accountType(),
                    HttpStatus.BAD_REQUEST);
        }
        return accountRepository.save(mapDtoToAccount(account));
    }

    protected Account mapDtoToAccount(AccountPOST_DTO dto) {
        Account account = new Account();
        String iban = createIBAN();
        while (isIbanPresent(iban)) {
            iban = createIBAN();
        }
        account.setUser(userRepository.findById(dto.userId()).orElseThrow(() -> new EntityNotFoundException("User not found")));
        account.setIBAN(iban);
        account.setBalance(dto.balance());
        account.setAbsoluteLimit(dto.absoluteLimit());
        account.setAccountType(dto.accountType());
        account.setIsActive(true);
        if (!account.getUser().getHasAccount()) {
            userHasAccount(userRepository.findById(dto.userId()).orElseThrow(() -> new EntityNotFoundException("User not found")));
        }
        return account;
    }

    protected boolean hasAccountOfType(Long userId, AccountType accountType) {
        // Implement the logic to check if the user already has an account of the specified type
        // You can query the account repository or perform any other necessary checks
        return accountRepository.existsByUserIdAndAccountType(userId, accountType);
    }

    protected Boolean isCustomer(Long userId) {
        if (userRepository.findById(userId).get().getUserType().equals(UserType.ROLE_EMPLOYEE)) {
            return false;
        }
        return true;
    }

    public AccountGET_DTO getAccountByUserId(long id) {
        return accountRepository.getAccountByUserId(id);
    }


    public boolean isUserHasNoActiveAccounts(long id) {
        List<Account> accounts = accountRepository.getAllAccountsByUserId(id);
        for (Account account : accounts) {
            if (account.getIsActive()) {
                return false;
            }
        }
        return true;
    }

    public String createIBAN() {
        String firstLetters = "NL";
        Random random = new Random();

        String randomNumber = String.format("%02d", random.nextInt(100));

        String lastLetters = "INHO0";
        String randomNumber2 = String.format("%09d", random.nextInt(1000000000));

        String iban = firstLetters + randomNumber + lastLetters + randomNumber2;

        return iban;
    }

    public Double getTotalBalanceByUserId(long id) {
        return accountRepository.getTotalBalanceByUserId(id);
    }


    public Account getAccountByIBAN(String IBAN) {
        if (!isIbanPresent(IBAN)) {
            throw new ApiRequestException("Iban not found " + IBAN, HttpStatus.NOT_FOUND);
        }
        return accountRepository.findAccountByIBAN(IBAN);
    }

    public boolean isIbanPresent(String iban) {
        return (accountRepository.findAccountByIBAN(iban) != null);
    }
}
