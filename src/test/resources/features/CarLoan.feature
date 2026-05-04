Feature: Car Loan EMI Calculator
  As a customer planning to buy a car
  I want to calculate the EMI for my car loan
  So that I can plan my monthly budget before taking the loan

  Background:
    Given user is on the EMI calculator website
    And user navigates to Car Loan section

  #   Scenario 1 — Problem statement requirement
  #   ₹15 Lakh @ 9.5% for 1 year
  @smoke @critical
  Scenario: Calculate EMI for a 15 Lakh car loan at 9.5 percent for 1 year
    When user enters loan amount as "1500000"
    And user enters interest rate as "9.5"
    And user enters tenure as "1" years
    Then the displayed EMI should match the calculated EMI for "Car_15L_9.5pct_1yr"
    And the first month principal and interest should be displayed

  #   Scenario 2 — Data-driven testing for multiple inputs
  @regression
  Scenario Outline: Calculate EMI for different car loan combinations
    When user enters loan amount as "<amount>"
    And user enters interest rate as "<rate>"
    And user enters tenure as "<tenure>" years
    Then the displayed EMI should match the calculated EMI for "<testName>"

    Examples:
      | amount  | rate | tenure | testName              |
      | 1500000 | 9.5  | 1      | Car_15L_9.5pct_1yr    |
      | 2000000 | 8.5  | 2      | Car_20L_8.5pct_2yr    |
      | 5000000 | 10   | 5      | Car_50L_10pct_5yr     |
      | 800000  | 11   | 3      | Car_8L_11pct_3yr      |

  #   Scenario 3 — Verify total interest payable
  @regression
  Scenario: Verify total interest is calculated correctly
    When user enters loan amount as "1500000"
    And user enters interest rate as "9.5"
    And user enters tenure as "1" years
    Then the total interest payable should be displayed
    And the total payment should equal loan amount plus total interest

  #   Scenario 4 — Edge case: high loan amount
  @edge
  Scenario: Calculate EMI for a high value car loan
    When user enters loan amount as "10000000"
    And user enters interest rate as "9"
    And user enters tenure as "7" years
    Then the displayed EMI should match the calculated EMI for "Car_1Cr_9pct_7yr"


  #   Scenario 5 — UI verification
  @ui
  Scenario: Verify all input fields are visible on Car Loan calculator
    Then the loan amount field should be visible
    And the interest rate field should be visible
    And the tenure field should be visible
    And the EMI result should be displayed