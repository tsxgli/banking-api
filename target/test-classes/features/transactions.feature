Feature: Getting all transactions
  Scenario: Getting all transactions
    Given I login as an employee
    When I request to get all transactions
    Then I should get all transactions
    Then I get a status code of 200

  Scenario: Getting a single transaction
    Given I login as a customer
    When I request to get a single transaction
    Then I should get a single transaction
    Then I get a status code of 200

  Scenario: Create a transaction with an invalid token
    Given I have an invalid token
    When I request to create a transaction
    Then I get a status code of 401

  Scenario: Deposit to selected account
    Given I login as a customer
    When I request to deposit to selected account
    Then I get a status code of 201


    Scenario: Withdraw from selected account
      Given I login as a customer
      And I want to withdraw from current account amount 200.0
      When I request to withdraw from selected account
      Then I get a status code of 201

      Scenario: Withdraw from selected account with insufficient funds
        Given I login as a customer
        And I want to withdraw from current account amount 200.0
        When I request to withdraw from selected account
        Then I get a status code of 403
