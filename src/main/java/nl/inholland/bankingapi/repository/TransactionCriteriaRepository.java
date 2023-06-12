package nl.inholland.bankingapi.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import nl.inholland.bankingapi.model.Transaction;
import nl.inholland.bankingapi.model.TransactionSearchCriteria;
import nl.inholland.bankingapi.model.pages.TransactionPage;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TransactionCriteriaRepository {
    private final TransactionRepository transactionRepository;
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;
    public TransactionCriteriaRepository(TransactionRepository transactionRepository, EntityManager entityManager) {
        this.transactionRepository = transactionRepository;
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    Predicate getPredicate(TransactionSearchCriteria searchCriteria, Root<Transaction> transactionRoot) {
        return criteriaBuilder.and(
                criteriaBuilder.like(transactionRoot.get("fromIban"), "%" + searchCriteria.getFromIban() + "%"),
                criteriaBuilder.like(transactionRoot.get("toIban"), "%" + searchCriteria.getToIban() + "%"),
                criteriaBuilder.greaterThanOrEqualTo(transactionRoot.get("timestamp"), searchCriteria.getFromDate()),
                criteriaBuilder.lessThanOrEqualTo(transactionRoot.get("timestamp"), searchCriteria.getToDate()),
                criteriaBuilder.greaterThanOrEqualTo(transactionRoot.get("amount"), searchCriteria.getGreaterThanAmount()),
                criteriaBuilder.lessThanOrEqualTo(transactionRoot.get("amount"), searchCriteria.getLessThanAmount()),
                criteriaBuilder.equal(transactionRoot.get("amount"), searchCriteria.getEqualToAmount())
        );
    }


}
