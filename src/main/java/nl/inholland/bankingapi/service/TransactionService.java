package nl.inholland.bankingapi.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import nl.inholland.bankingapi.exception.ApiRequestException;
import nl.inholland.bankingapi.filter.JwtTokenFilter;
import nl.inholland.bankingapi.jwt.JwtTokenProvider;
import nl.inholland.bankingapi.model.*;
import nl.inholland.bankingapi.model.dto.TransactionDepositDTO;
import nl.inholland.bankingapi.model.dto.TransactionGET_DTO;
import nl.inholland.bankingapi.model.dto.TransactionPOST_DTO;
import nl.inholland.bankingapi.model.dto.TransactionWithdrawDTO;
import nl.inholland.bankingapi.model.specifications.TransactionSpecifications;
import nl.inholland.bankingapi.repository.AccountRepository;
import nl.inholland.bankingapi.repository.TransactionCriteriaRepository;
import nl.inholland.bankingapi.repository.TransactionRepository;
import nl.inholland.bankingapi.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final AccountService accountService;
    private final TransactionCriteriaRepository transactionCriteriaRepository;
    private final TransactionSpecifications transactionSpecifications;
    private final HttpServletRequest request;

    private final AccountRepository accountRepository;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenFilter jwtTokenFilter;
    private static final String BANK_IBAN = "NL01INHO0000000001";

    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository,
                              ModelMapper modelMapper,
                              AccountRepository accountRepository,
                              EntityManager entityManager, AccountService accountService,
                              TransactionCriteriaRepository transactionCriteriaRepository,
                              TransactionSpecifications transactionSpecifications, HttpServletRequest request, AccountRepository accountRepository1, UserService userService, JwtTokenProvider jwtTokenProvider, JwtTokenFilter jwtTokenFilter) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.entityManager = entityManager;
        this.accountService = accountService;
        this.transactionCriteriaRepository = transactionCriteriaRepository;
        this.transactionSpecifications = transactionSpecifications;
        this.request = request;
        this.accountRepository = accountRepository1;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtTokenFilter = jwtTokenFilter;
    }

    public List<TransactionGET_DTO> getAllTransactions(String fromIban, String toIban, String fromDate, String toDate, Double lessThanAmount, Double greaterThanAmount, Double equalToAmount, TransactionType type, Long performingUser, Date searchDate) {
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Transaction> specification = TransactionSpecifications.getSpecifications(fromIban, toIban, fromDate, toDate,
                lessThanAmount, greaterThanAmount, equalToAmount);

        List<TransactionGET_DTO> allTransactions = new ArrayList<>();
        List<TransactionGET_DTO> userTransactions = new ArrayList<>();

        for (Transaction transaction : transactionRepository.findAll(specification, pageable)) {
            allTransactions.add(convertTransactionResponseToDTO(transaction));
            //if the transaction is performed by the logged-in user, add it to the userTransactions list
            if (transaction.getPerformingUser().getId().equals(userService.getLoggedInUser(request).getId())) {
                userTransactions.add(convertTransactionResponseToDTO(transaction));
                getSumOfAllTransactionsFromTodayByAccount(request);
            }
        }

        //if the logged-in user is an employee, then show all transactions else show only user transactions
        if (userService.getLoggedInUser(request).getUserType().equals(UserType.ROLE_CUSTOMER)) {
            return userTransactions;
        } else if (userService.getLoggedInUser(request).getUserType().equals(UserType.ROLE_EMPLOYEE)) {
            return allTransactions;
        }
        return allTransactions;
    }

    public Transaction addTransaction(@NotNull TransactionPOST_DTO transactionPOSTDto) {
        try {
            Account senderAccount = accountService.getAccountByIBAN(transactionPOSTDto.fromIban());
            Account receiverAccount = accountService.getAccountByIBAN(transactionPOSTDto.toIban());

            //transfer money from sender to receiver and update balances
            checkTransaction(transactionPOSTDto, senderAccount, receiverAccount);
            transferMoney(senderAccount, receiverAccount, transactionPOSTDto.amount());

            //save transaction to transaction repository
            return transactionRepository.save(mapTransactionToPostDTO(transactionPOSTDto));
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Transaction could not be completed " + e.getMessage());
        }
    }

    private void transferMoney(Account senderAccount, Account receiverAccount, Double amount) {
        //subtract money from the sender and save
        senderAccount.setBalance(senderAccount.getBalance() - amount);
        receiverAccount.setBalance(receiverAccount.getBalance() + amount);
        // Save the updated receiver account
        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);
    }

    public TransactionGET_DTO getTransactionById(long id) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        if (optionalTransaction.isPresent()) {
            return convertTransactionResponseToDTO((optionalTransaction.get()));
        } else {
            throw new EntityNotFoundException("Transaction with the specified ID not found.");
        }
    }

    public Transaction mapTransactionToPostDTO(TransactionPOST_DTO postDto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(postDto.amount());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setPerformingUser(userService.getUserById(userService.getLoggedInUser(request).getId()));
        transaction.setToIban(accountService.getAccountByIBAN(postDto.toIban()));
        transaction.setFromIban(accountService.getAccountByIBAN(postDto.fromIban()));
        transaction.setType(postDto.type());
        return transaction;
    }

    public TransactionGET_DTO convertTransactionResponseToDTO(Transaction transaction) {
        return new TransactionGET_DTO(
                transaction.getId(),
                transaction.getFromIban().getIBAN(),
                transaction.getToIban().getIBAN(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getTimestamp().toString(),
                transaction.getPerformingUser().getId()
        );
    }

    private void checkTransaction(TransactionPOST_DTO transaction, Account fromAccount, Account toAccount) {
        User performingUser = userService.getLoggedInUser(request);
        User receiverUser = userService.getUserById(toAccount.getUser().getId());
        User senderUser = userService.getUserById(performingUser.getId());
        if (transaction.amount() <= 0) {
            throw new ApiRequestException("Amounts cannot be 0 or less", HttpStatus.NOT_ACCEPTABLE);
        }
        if (fromAccount.getBalance() < transaction.amount()) {
            throw new ApiRequestException("You do not have enough money to perform this transaction", HttpStatus.BAD_REQUEST);
        }
        if (fromAccount.getIBAN().equals(toAccount.getIBAN())) {
            throw new ApiRequestException("You cannot transfer money to the same account", HttpStatus.BAD_REQUEST);
        }
        if (accountIsSavingsAccount(fromAccount) && !userIsOwnerOfAccount(performingUser, fromAccount) && transaction.type() == TransactionType.WITHDRAWAL) {
            throw new ApiRequestException("You do not own the savings account you are trying to withdraw from", HttpStatus.FORBIDDEN);
        }
        
        if (!userIsOwnerOfAccount(senderUser, fromAccount) && (!userIsEmployee(senderUser)) && (!transactionIsWithdrawalOrDeposit(transaction))) {

            throw new ApiRequestException("You are not the owner of the account you are trying to transfer money from", HttpStatus.FORBIDDEN);
        }
        if (!userIsOwnerOfAccount(receiverUser, toAccount) && (!userIsEmployee(senderUser)) && !transactionIsWithdrawalOrDeposit(transaction)) {
            throw new ApiRequestException("You are not the owner of the account you are trying to transfer money to", HttpStatus.FORBIDDEN);
        }
        if (performingUser.getTransactionLimit() < transaction.amount()) {
            throw new ApiRequestException("You have exceeded your transaction limit", HttpStatus.FORBIDDEN);
        }
        if ((getSumOfAllTransactionsFromTodayByAccount(request) + transaction.amount() > performingUser.getDailyLimit())) {
            throw new ApiRequestException("You have exceeded your daily limit", HttpStatus.BAD_REQUEST);
        }
        if (!fromAccount.getIsActive()) {
            throw new ApiRequestException("Sender account cannot be a CLOSED account.", HttpStatus.BAD_REQUEST);
        }
        if (!toAccount.getIsActive()) {
            throw new ApiRequestException("Receiving account cannot be a CLOSED account.", HttpStatus.BAD_REQUEST);
        }
        if (((fromAccount.getBalance()) - transaction.amount()) < toAccount.getAbsoluteLimit())
            throw new ApiRequestException("You can't have that little money in your account!", HttpStatus.BAD_REQUEST);

    }

    public Transaction withdraw(TransactionWithdrawDTO dto) {
        return addTransaction(new TransactionPOST_DTO(
                dto.fromIban(),
                BANK_IBAN,
                dto.amount(),
                TransactionType.WITHDRAWAL,
                userService.getLoggedInUser(request).getId()));
    }

    public Transaction deposit(TransactionDepositDTO dto) {
        return addTransaction(new TransactionPOST_DTO(
                BANK_IBAN,
                dto.toIban(),
                dto.amount(),
                TransactionType.DEPOSIT,
                userService.getLoggedInUser(request).getId()));
    }

    private boolean accountIsSavingsAccount(Account account) {
        return account.getAccountType() == AccountType.SAVINGS;
    }

    private boolean userIsEmployee(User user) {
        return user.getUserType() == UserType.ROLE_EMPLOYEE;
    }

    private boolean userIsOwnerOfAccount(User user, Account account) {
        return Objects.equals(user.getId(), account.getUser().getId());
    }

    private boolean accountIsBankAccount(Account account) {
        return account.getIBAN().equals(BANK_IBAN);
    }

    private boolean transactionIsWithdrawalOrDeposit(TransactionPOST_DTO transaction) {
        return transaction.type() == TransactionType.WITHDRAWAL || transaction.type() == TransactionType.DEPOSIT;
    }

    public Double getSumOfAllTransactionsFromTodayByAccount(HttpServletRequest request) {
        User user = userService.getLoggedInUser(request);
        List<Transaction> transactions = transactionRepository.findAllByPerformingUserAndTimestampBetween(user, LocalDate.now().atTime(0, 0), LocalDate.now().atTime(23, 59));
        double totalAmount = 0.0;
        for (Transaction transaction : transactions) {
            totalAmount += transaction.getAmount();
        }
        return totalAmount;
    }
}