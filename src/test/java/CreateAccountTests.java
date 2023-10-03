import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.opentest4j.AssertionFailedError;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


public class CreateAccountTests {
    private static final String URL = "https://www.facebook.com/";

    private static final Logger logger = LogManager.getLogger(CreateAccountTests.class);
    private static WebDriver wd;

    private final String createAccountXpath = "//a[@data-testid='open-registration-form-button']";
    private final String firstNameXpath = "//input[@name='firstname']";
    private final String lastNameXpath = "//input[@name='lastname']";
    private final String mobileOrEmailXpath = "//input[@name = 'reg_email__' ]";
    private final String retypeEmailXpath = "//input[@name='reg_email_confirmation__']";
    private final String passwordXpath = "//input[@id='password_step_input']";
    private final String submitButtonXpath = "//button[text()='Sign Up']";
    private final String customGenderXpath = "//label[text()='Custom']";
    private final String preferredPronounXpath = "//select[@name='preferred_pronoun']";
    private final String customGenderManualInputXpath = "//input[@name='custom_gender']";
    private final String closePopupXpath = "//img[@class='_8idr img']";
    private final String birthdayMonthXpath = "//select[@name='birthday_month']";
    private final String birthdayDayXpath = "//select[@name='birthday_day']";
    private final String birthdayYearXpath = "//select[@name='birthday_year']";
    private final String femaleGenderXpath = "//label[text()='Female']";
    private final String maleGenderXpath = "//label[text()='Male']";

    @BeforeAll
    public static void openPage() {
        wd = Driver.getWebDriver();
        wd.get(URL);
        wd.manage().timeouts().implicitlyWait(Duration.ofMillis(5000));
        logger.info("Started - Autotests for facebook create account");
    }

    @AfterAll
    public static void closePage(){
        wd.quit();
    }

    @BeforeEach
    public void openCreateAccountPopup() {
        try {
            WebElement createAccountButton = wd.findElement(By.xpath(createAccountXpath));
            assertNotNull(createAccountButton, "Create account button was not found");
            WebDriverWait wait = new WebDriverWait(wd, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.elementToBeClickable(createAccountButton));
            createAccountButton.click();
        } catch (AssertionFailedError e) {
            logger.error("Assertion failed - Create account button was not found");
            throw e;
        }
    }

    @AfterEach
    public void closeSignUpPopup() {
        try {
            WebElement closeSignUpPopup = wd.findElement(By.xpath(closePopupXpath));
            closeSignUpPopup.click();
            WebElement createAccountButton = wd.findElement(By.xpath(createAccountXpath));
            assertNotNull(createAccountButton, "Create account button was not found");
        } catch (AssertionFailedError | NoSuchElementException e) {
            logger.error("Assertion failed - Create Account popup was not closed or Create Account button didn't appear");
            throw e;
        }
    }

    @Test
    public void signUpPopUpAppeared() {
        try {
            WebElement signUpButton = wd.findElement(By.xpath(submitButtonXpath));
            assertNotNull(signUpButton, "Sign up button was not found");
            logger.info("Sign up button appeared - success");
        } catch (AssertionFailedError | NoSuchElementException e) {
            logger.error("Assertion failed - sign up button was not found");
            throw e;
        }
    }

    @Test
    public void signUpPopUpContainsNecessaryFields() {
        Set<String> elements = new HashSet<>();
        elements.add(firstNameXpath);
        elements.add(lastNameXpath);
        elements.add(mobileOrEmailXpath);
        elements.add(passwordXpath);
        elements.add(birthdayMonthXpath);
        elements.add(birthdayDayXpath);
        elements.add(femaleGenderXpath);
        elements.add(birthdayYearXpath);
        elements.add(maleGenderXpath);
        elements.add(customGenderXpath);
        for (String item : elements) {
            elementIsPresent(item);
        }
    }

    @ParameterizedTest
    @MethodSource("emptyFields")
    public void registrationWithEmptyFields(String firstName, String lastName, String email, String retypeEmail, String password) {
        try {
            fillRegistrationForm(firstName, lastName, email, retypeEmail, password);
            submitButtonClick();
        } catch (AssertionFailedError | NoSuchElementException e) {
            logger.error("Registration with necessary fields test failed: " + e.getMessage());
            throw e;
        }
    }

    @ParameterizedTest
    @MethodSource("specialCharactersInFields")
    public void registrationWithSpecialCharactersInFields(String firstName, String lastName, String email, String retypeEmail, String password) {
        try {
            fillRegistrationForm(firstName, lastName, email, retypeEmail, password);
            submitButtonClick();
        } catch (AssertionFailedError | NoSuchElementException e) {
            logger.error("Registration with special characters test failed: " + e.getMessage());
            throw e;
        }
    }

    @ParameterizedTest
    @MethodSource("invalidEmail")
    public void registrationWithInvalidEmail(String firstName, String lastName, String email, String retypeEmail, String password) {
        try {
            fillRegistrationForm(firstName, lastName, email, retypeEmail, password);
            assertThrows(ElementNotInteractableException.class, () -> wd.findElement(By.xpath(retypeEmailXpath)).sendKeys("sdf"));
            submitButtonClick();
        } catch (AssertionFailedError | NoSuchElementException e) {
            logger.error("Registration with invalid email test failed" + e.getMessage());
            throw e;
        }
    }

    private void submitButtonClick() {
        WebElement submitButton = wd.findElement(By.xpath(submitButtonXpath));
        assertNotNull(submitButton);
        submitButton.click();
    }

    @Test
    public void retypeEmailFieldAppearsAfterEnteringEmail() {
        try {
            WebElement mobileOrEmail = wd.findElement(By.xpath(mobileOrEmailXpath));
            WebElement retypeEmail = wd.findElement(By.xpath(retypeEmailXpath));
            mobileOrEmail.sendKeys("1234567");
            assertThrows(ElementNotInteractableException.class, () -> retypeEmail.sendKeys("sdf"));
            mobileOrEmail.sendKeys("test@test.com");
            assertDoesNotThrow(() -> retypeEmail.sendKeys("sdf"));
        } catch (AssertionFailedError | NoSuchElementException e) {
            logger.error("Retype email field appearance test failed" + e.getMessage());
            throw e;
        }
    }

    @Test
    public void customGenderFieldsAppear() {
        try {
            wd.findElement(By.xpath(customGenderXpath)).click();
            assertNotNull(wd.findElement(By.xpath(preferredPronounXpath)));
            assertNotNull(wd.findElement(By.xpath(customGenderManualInputXpath)));
        } catch (AssertionFailedError | NoSuchElementException e) {
            logger.error("Assertion of customer gender fields failed: " + e.getMessage());
            throw e;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345", "asdfg", "ASDER", "sdf123", "!!!!!!", "&&&&&&", "!&!&!&!&", "asdf!&", "&!34?36"})
    public void invalidPassword(String password) {
        try {
            fillRegistrationForm("Olga", "Novikova", "test@test.com", "test@test.com", password);
            submitButtonClick();
        } catch (AssertionFailedError | NoSuchElementException e) {
            logger.error("Assertion of password failed: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void longAndShortValuesTest() {
        try {
            String longString = "fsdfsdpofkogdfiogssdfksdekreowppopcoefksdpofkspodfkcsldkmxcvjghskdnjkxvnzklcmisdfhhdfshvkdsjiojhohidnjncisodfjsdifhsuidfsjkdnkdmcospdksioruirhfskjmnkcmozckoujeiojuihsnkldcmosjfiosdfjsocdmioscisdfjiosdjcsdiocmdmcdsicosdfjodmiopcspoiaojfcoefmviefmviodslcpost";
            String shortString = "g";
            String longEmail = longString + "@google.com";
            String shortEmail = "y@y.c";
            String longPassword = "fsdfsdpofkogdfiogssdfksdekreowppopcoefk&dpofkspodfkcsldkmxcvjghskdnjkxvnzklcmisdfhhdfshvkdssd234234ojhohidnjncisodfjsdifhsuidfsjkdnkdmcospdksioruirhfskjmn!cmWERjeiojuihsnkldcmosjfiosdfjsocdmioscisdfjiosdjcsdiocmdmcdsicosdfjodmiopcspoiaojfcoefmvie!&viodslcp";
            fillRegistrationForm(longString, longString, longEmail, longEmail, longPassword);
            submitButtonClick();
            fillRegistrationForm(shortString, shortString, shortEmail, shortEmail, "sdfs#@&");
            submitButtonClick();
        } catch (AssertionFailedError | NoSuchElementException e) {
            logger.error("Assertion of long and short values in name, email and password failed: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void emailAndRetypeEmailAreDifferent() {
        try {
            fillRegistrationForm("Olga", "Tester", "tester@test.ru", "another@mail.com", "password34Q!");
            submitButtonClick();
        } catch (AssertionFailedError | NoSuchElementException e) {
            logger.error("Assertion of email and retype email fields failed: " + e.getMessage());
            throw e;
        }
    }

    private static Stream<Arguments> invalidEmail() {
        return Stream.of(
                Arguments.of("Name", "Surname", "sdfer.com", "", "QwertY45"),
                Arguments.of("Name", "Surname", "gdfgdfg@sfsdf", "", "QwertY45")
        );
    }

    private static Stream<Arguments> specialCharactersInFields() {
        return Stream.of(
                Arguments.of("@#$!%^&*()?/.,<>", "Surname", "1234567", "", "QwertY45"),
                Arguments.of("Name", "@#$!%^&*()?/.,<>", "1234567", "", "QwertY45"),
                Arguments.of("Name", "Surname", "@#$!%^&*()?/.,<>", "", "QwertY45"),
                Arguments.of("Name", "Surname", "1234567", "", "@#$!%^&*()?/.,<>"),
                Arguments.of("Name", "Surname", "@#$!%^&*()?/.,@sdf@#$!%^&*()?/.,s.com", "@#$!%^&*()?/.,@sdf@#$!%^&*()?/.,s.com", "QwertY45")
        );
    }

    private static Stream<Arguments> emptyFields() {
        return Stream.of(
                Arguments.of("", "Surname", "1234567", "", "QwertY45"),
                Arguments.of("Name", "", "1234567", "", "QwertY45"),
                Arguments.of("Name", "Surname", "", "", "QwertY45"),
                Arguments.of("Name", "Surname", "1234567", "", ""),
                Arguments.of(" ", "Surname", "1234567", "", "QwertY45"),
                Arguments.of("Name", " ", "1234567", "", "QwertY45"),
                Arguments.of("Name", "Surname", " ", "", "QwertY45"),
                Arguments.of("Name", "Surname", "1234567", "", " "),
                Arguments.of("Name", "Surname", "sdfsdf@gmail.com", "", "QwertY45"),
                Arguments.of("", "", "", "", "")
        );
    }

    private void fillRegistrationForm(String firstName, String lastName, String email, String retypeEmail, String password) {
        wd.findElement(By.xpath(firstNameXpath)).sendKeys(firstName);
        wd.findElement(By.xpath(lastNameXpath)).sendKeys(lastName);
        wd.findElement(By.xpath(mobileOrEmailXpath)).sendKeys(email);
        if (!retypeEmail.isEmpty()) {
            wd.findElement(By.xpath(retypeEmailXpath)).sendKeys(retypeEmail);
        }
        wd.findElement(By.xpath(passwordXpath)).sendKeys(password);
    }

    private void elementIsPresent(String xpath) {
        String errorMessage = "Element by xpath = %s was not found";
        try {
            WebElement element = wd.findElement(By.xpath(xpath));
            assertNotNull(element, String.format(errorMessage, xpath));
        } catch (AssertionFailedError | NoSuchElementException e) {
            logger.error("Assertion failed - " + String.format(errorMessage, xpath));
            throw e;
        }
    }

}
