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
import java.nio.charset.StandardCharsets;


import static org.junit.jupiter.api.Assertions.assertTrue;

public class CsvHttpTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/test-data.csv", numLinesToSkip = 0)
    public void testCsvSearch(String query) throws IOException {

        // Set up ChromeOptions for headless mode
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run in headless mode
        options.addArguments("--disable-gpu"); // Disable GPU usage (optional for headless)
        options.addArguments("--no-sandbox"); // Recommended for Linux environments
        options.addArguments("--disable-dev-shm-usage"); // Handle resource constraints

        // Set up the WebDriver (ensure you have the correct driver for your browser)
        String chromeDriverPath = getClass().getClassLoader().getResource("chromedriver-linux64/chromedriver").getPath();
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        WebDriver driver = new ChromeDriver(options);

        try {
            // Open the webpage
            String encodedQuery = URLEncoder.encode(query);
            driver.get("https://cs2.nhsd.io/services/service-catalogue?query=" + encodedQuery);

            // Locate the element containing the number of results (e.g., "1 results")
            WebElement resultsElement = driver.findElement(By.id("search-results-count"));

            // Extract the text (e.g., "1 results") and parse the number
            String resultsText = resultsElement.getText();
            int resultsCount = Integer.parseInt(resultsText.split(" ")[0]);

            // Assert that the results count is greater than 0
            assertTrue(resultsCount > 0, "Test failed for query: " + query + ". Number of results: " + resultsCount);

        } finally {
            // Close the browser
            driver.quit();
        }
    }
}
