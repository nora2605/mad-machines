# Notes for Testing

## unit tests
- logic checks
- boundary checks
- error handling
- object-oriented checks
- black/white box tests

### how they work
assertEquals, assertTrue/assertFalse, assertNotNull check condition, otherwise test fails
assumeTrue, assumingThat test if condition is met, skip otherwise
@ParameterizedTest uses all the @ValueSource() to check for eg a set of numbers
@TestFactory can generate a whole list of similar Test Cases at run time
@Tag("important") etc can be used to sort tests
@BeforeEach to re-set test object
@Disabled disables the test

### best practices
- use framework
- automate
- assert once
- implement early (tdd)

## gui
not necessary

## level

## model

## scene

### GameObjects