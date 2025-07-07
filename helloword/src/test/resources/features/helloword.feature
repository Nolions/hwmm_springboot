Feature: Helloword API

  Scenario: Call /hell api
  then get Hello, world! response
    Given server running
    When call "hello" API
    Then get "Hello, world!" response

  Scenario: Call /hi api
  then get Hi response
    Given server running
    When call "hi" API
    Then get "Hi" response