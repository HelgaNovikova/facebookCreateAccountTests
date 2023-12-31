import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.opentest4j.AssertionFailedError;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private final String customGenderXpath = "//*[text()='Custom']//following-sibling::*[@type='radio']";
    private final String preferredPronounXpath = "//select[@name='preferred_pronoun']";
    private final String customGenderManualInputXpath = "//input[@name='custom_gender']";
    private final String birthdayMonthXpath = "//select[@name='birthday_month']";
    private final String birthdayDayXpath = "//select[@name='birthday_day']";
    private final String birthdayYearXpath = "//select[@name='birthday_year']";
    private final String femaleGenderXpath = "//*[text()='Female']//following-sibling::*[@type='radio']";
    private final String maleGenderXpath = "//*[text()='Male']//following-sibling::*[@type='radio']";

    @BeforeAll
    public static void openPage() {
        wd = Driver.getWebDriver();
        wd.manage().timeouts().implicitlyWait(Duration.ofMillis(5000));
        logger.info("Started - Autotests for facebook create account");
    }

    @AfterAll
    public static void closePage() {
        Driver.quitDriver();
    }

    @BeforeEach
    public void openCreateAccountPopup() {
        try {
            wd.get(URL);
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

    @Test
    public void fillFieldsErrorMessages() {
        submitButtonClick();
        wd.findElement(By.xpath(firstNameXpath)).click();
        assertNotNull(wd.findElement(By.xpath("//*[contains(text(),'your name')]")));
        wd.findElement(By.xpath(lastNameXpath)).click();
        assertNotNull(wd.findElement(By.xpath("//*[contains(text(), 'your name')]")));
        wd.findElement(By.xpath(mobileOrEmailXpath)).click();
        assertNotNull(wd.findElement(By.xpath("//*[contains(text(), 'to reset your password')]")));
        wd.findElement(By.xpath(passwordXpath)).click();
        assertNotNull(wd.findElement(By.xpath("//*[contains(text(), 'at least six numbers')]")));
        wd.findElement(By.xpath(birthdayDayXpath)).click();
        assertNotNull(wd.findElement(By.xpath("//*[contains(text(), 'real birthday')]")));
        wd.findElement(By.xpath(birthdayMonthXpath)).click();
        assertNotNull(wd.findElement(By.xpath("//*[contains(text(), 'real birthday')]")));
        wd.findElement(By.xpath(birthdayYearXpath)).click();
        assertNotNull(wd.findElement(By.xpath("//*[contains(text(), 'real birthday')]")));
        wd.findElement(By.xpath("//span[@data-name='gender_wrapper']/../i[1]")).click();
        assertNotNull(wd.findElement(By.xpath("//*[contains(text(), 'choose a gender')]")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1990", "2023", "1905"})
    public void yearTest(String year) {
        wd.findElement(By.xpath(birthdayYearXpath)).click();
        wd.findElement(By.xpath("//*[text() = '" + year + "']")).click();
        String yearValue = wd.findElement(By.xpath(birthdayYearXpath)).getAttribute("value");
        assertEquals(year, yearValue);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Oct", "Jan", "Dec"})
    public void monthTest(String month) {
        Select select = new Select(wd.findElement(By.xpath(birthdayMonthXpath)));
        select.selectByVisibleText(month);
        List<WebElement> selectedOptions = select.getAllSelectedOptions();
        assertEquals(month, selectedOptions.get(0).getText());
    }

    @ParameterizedTest
    @ValueSource(strings = {femaleGenderXpath, maleGenderXpath, customGenderXpath})
    public void genderTest(String xpath) {
        WebElement we = wd.findElement(By.xpath(xpath));
        we.click();
        String isChecked = we.getAttribute("checked");
        assertNotNull(isChecked);
        assertEquals(isChecked, "true");
    }

    @ParameterizedTest
    @MethodSource("newPageOpen")
    public void termsWindowIsOpened(String xpath, String expectedUrlPart) {
        wd.findElement(By.xpath(xpath)).click();
        ArrayList<String> tabs = new ArrayList<>(wd.getWindowHandles());
        wd.switchTo().window(tabs.get(1));
        String url = wd.getCurrentUrl();
        assertTrue(url.contains(expectedUrlPart));
        wd.close();
        wd.switchTo().window(tabs.get(0));
    }

    private static Stream<Arguments> newPageOpen() {
        return Stream.of(
                Arguments.of("//a[@id='terms-link']", "terms"),
                Arguments.of("//a[@id='privacy-link']", "privacy"),
                Arguments.of("//a[@id='cookie-use-link']", "cookies")
        );
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
