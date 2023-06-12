Feature: Getting all accounts
  Scenario: Getting all accounts
    Given employee logs in
    When I request to get all accounts
    Then I should get all accounts
    Then getting a status code of 200

  Scenario: Getting a single account
    Given employee logs in
    When I request to get a single account
    Then I should get a single account
    Then getting a status code of 200

  Scenario: Getting a bank account
    Given employee logs in
    And I request to get a bank account
    When I should get an api request exception
    Then getting a status code of 404

  Scenario: Getting all account owned by a customer
    Given customer logs in
    And I request to get all accounts
    Then I should get all accounts as customer
    Then getting a status code of 200

  Scenario: deactivate account with ID
    Given employee logs in
    When I request to deactivate account with ID
    Then I should deactivate account with ID
    Then getting a status code of 200

  Scenario: activate account with ID
    Given employee logs in
    When I request to activate account with ID
    Then I should activate account with ID
    Then getting a status code of 200

  Scenario: modify absoluteLimit with ID
    Given employee logs in
    When I request to modify absoluteLimit with ID
    Then getting a status code of 200
