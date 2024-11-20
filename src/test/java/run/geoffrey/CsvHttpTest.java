package run.geoffrey;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CsvHttpTest {

    private static final String TEST_SUBJECT = "https://cs2.nhsd.io/";

    @ParameterizedTest
    @CsvFileSource(resources = "/test-data.csv", numLinesToSkip = 0)
    public void testCsvSearch(String query, String url) throws IOException {

        // Set up ChromeOptions for headless mode
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run in headless mode
        options.addArguments("--disable-gpu"); // Disable GPU usage (optional for headless)
        options.addArguments("--no-sandbox"); // Recommended for Linux environments
        options.addArguments("--disable-dev-shm-usage"); // Handle resource constraints

        // Set up the WebDriver
        String chromeDriverPath = getClass().getClassLoader().getResource("chromedriver-linux64/chromedriver").getPath();
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        WebDriver driver = new ChromeDriver(options);

        try {
            // Encode the query for URL safety
            String encodedQuery = URLEncoder.encode(query);

            // Construct the expected URL
            String expectedUrl = url.startsWith("/") ? TEST_SUBJECT + url.substring(1) : url;

            // Open the webpage
            driver.get(TEST_SUBJECT + "services/service-catalogue?query=" + encodedQuery);

            // Locate the element containing the number of results (e.g., "1 results")
            WebElement resultsElement = driver.findElement(By.id("search-results-count"));

            // Extract the text (e.g., "1 results") and parse the number
            String resultsText = resultsElement.getText();
            int resultsCount = Integer.parseInt(resultsText.split(" ")[0]);

            // Assert that the results count is greater than 0
            assertTrue(resultsCount > 0, "Test failed for query: " + query + ". Number of results: " + resultsCount);

            // Locate all result links on the page
            List<WebElement> resultLinks = driver.findElements(By.cssSelector("a.nhsd-a-link"));

            // Flag to track if a matching link is found
            boolean isMatchFound = false;

            // Iterate through all result links
            for (WebElement link : resultLinks) {
                String actualUrl = link.getAttribute("href");

                // Check if the current link matches the expected URL
                if (actualUrl.equals(expectedUrl)) {
                    isMatchFound = true;
                    break;
                }
            }

            // Assert that at least one matching link was found
            assertTrue(isMatchFound,
                    "No matching URL found for query: " + query + ". Expected: " + expectedUrl);

        } finally {
            // Close the browser
            driver.quit();
        }
    }
}
