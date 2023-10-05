import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DavikTests {
    private static WebDriver wd;
    private static final String URL = "https://daviktapes.com/";

    private final String companyXpath = "//a[text()='Company']";
    private final String productsXpath = "//a[text()='Products']";
    private final String industriesXpath = "//a[text()='Industries']";
    private final String knowledgeCenterXpath = "//a[text()='Knowledge Center']";
    private final String contactXpath = "//a[text()='CONTACT']";
    private final String homeXpath = "//a[text()='Home']";

    @BeforeAll
    public static void openPage() {
        wd = Driver.getWebDriver();
        wd.navigate().to(URL);
        wd.manage().window().fullscreen();
        wd.manage().timeouts().implicitlyWait(Duration.ofMillis(5000));
    }

    @Test
    public void mainMenu() {
        WebDriverWait wait = new WebDriverWait(wd, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(companyXpath)));
        assertNotNull(wd.findElement(By.xpath(companyXpath)));
        assertNotNull(wd.findElement(By.xpath(productsXpath)));
        assertNotNull(wd.findElement(By.xpath(industriesXpath)));
        assertNotNull(wd.findElement(By.xpath(knowledgeCenterXpath)));
        assertNotNull(wd.findElement(By.xpath(contactXpath)));
        assertNotNull(wd.findElement(By.xpath(homeXpath)));
    }

    @Test
    public void companySubmenu() {
        Actions actions = new Actions(wd);
        actions.moveToElement(wd.findElement(By.xpath(companyXpath))).build().perform();
        assertTrue(wd.findElement(By.xpath("//a[text()='About us']")).isDisplayed());
        assertTrue(wd.findElement(By.xpath("//a[text()='Our vision']")).isDisplayed());
        assertTrue(wd.findElement(By.xpath("//a[text()='Our Team']")).isDisplayed());
        assertTrue(wd.findElement(By.xpath("//a[text()='Quality']")).isDisplayed());
        assertTrue(wd.findElement(By.xpath("//a[text()='R&D']")).isDisplayed());
        assertTrue(wd.findElement(By.xpath("//a[text()='Sustainability']")).isDisplayed());
    }

    @Test
    public void productsSubmenu() {
        Actions actions = new Actions(wd);
        actions.moveToElement(wd.findElement(By.xpath(productsXpath))).build().perform();
        assertTrue(wd.findElement(By.xpath("//a[text()='Carry Handle Tape']")).isDisplayed());
        assertTrue(wd.findElement(By.xpath("//a[text()='Resealable Finger Lift tape']")).isDisplayed());
        assertTrue(wd.findElement(By.xpath("//a[text()='Splicing Tapes']")).isDisplayed());
        assertTrue(wd.findElement(By.xpath("//a[text()='Pest Control']")).isDisplayed());
        assertTrue(wd.findElement(By.xpath("//a[text()='White board sticker']")).isDisplayed());
        assertTrue(wd.findElement(By.xpath("//a[text()='Low tack tapes']")).isDisplayed());
        assertTrue(wd.findElement(By.xpath("//a[text()='Deep freezer tape']")).isDisplayed());
        assertTrue(wd.findElement(By.xpath("//a[text()='Printable tapes']")).isDisplayed());
        assertTrue(wd.findElement(By.xpath("//a[text()='Double sided tapes']")).isDisplayed());
        assertTrue(wd.findElement(By.xpath("//a[text()='One Sided Tapes']")).isDisplayed());
        assertTrue(wd.findElement(By.xpath("//a[text()='Developing Customized Products']")).isDisplayed());
    }

    @Test
    public void industriesSubmenu() {
        Actions actions = new Actions(wd);
        actions.moveToElement(wd.findElement(By.xpath(industriesXpath))).build().perform();
        assertTrue(wd.findElement(By.xpath("//a[text()='Food & Beverages']")).isDisplayed());
        assertTrue(wd.findElement(By.xpath("//a[text()='Non Woven']")).isDisplayed());
        assertTrue(wd.findElement(By.xpath("//a[text()='Agriculture']")).isDisplayed());
        assertTrue(wd.findElement(By.xpath("//a[text()='Hygiene']")).isDisplayed());
        assertTrue(wd.findElement(By.xpath("//a[text()='Retail']")).isDisplayed());
        assertTrue(wd.findElement(By.xpath("//a[text()='Building & DIY']")).isDisplayed());
    }

    @Test
    public void knowledgeCenterSubmenu() {
        Actions actions = new Actions(wd);
        actions.moveToElement(wd.findElement(By.xpath(knowledgeCenterXpath))).build().perform();
        assertTrue(wd.findElement(By.xpath("//a[text()='Articles']")).isDisplayed());
        assertTrue(wd.findElement(By.xpath("//a[text()='Events']")).isDisplayed());
    }

    @AfterAll
    public static void closePage() {
        wd.quit();
    }
}
