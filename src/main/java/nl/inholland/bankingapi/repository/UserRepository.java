package nl.inholland.bankingapi.repository;

import nl.inholland.bankingapi.model.Transaction;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.dto.UserGET_DTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findAll(Specification<User> specification, Pageable pageable);


    List<User> findUserByAccountsAccountId(long id);

    Optional<User> findUserByEmail(String email);

    User findUserById(long id);

    void deleteUserById(long id);

    List<User> findAllByHasAccount(boolean hasAccount);
}
