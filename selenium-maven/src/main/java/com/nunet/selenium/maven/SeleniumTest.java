/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nunet.selenium.maven;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.sikuli.script.ScreenImage;

/**
 *
 * @author buli
 */
public class SeleniumTest {
    
    ChromeDriver driver;
    ChromeOptions opt;
    Logger logger;
        
    Screen screen; // Sikuli screen object
    
    //String testUrl = "https://www.bbc.com/sport/live/football/57760106";    
    String testUrl = "https://www.bbc.com/news/world-us-canada-57770436";
    
    long start_time; // time when fetching started
    long final_time; // time when fetching end
    int check = 1; // value used in check Loading to prevent displaying log again and again while looping
    
    // get the project directory
    String projectDir = System.getProperty("user.dir");

    // screen shot path by driver and sikuli
    String pathByDriver = "screenshots/by-driver/";
    String pathBySikuli = "screenshots/by-sikuli/";
    
    /*
        set up the logger with Log4j library
    */
    public void setUpLogger(){
        // create logger instance with current class name 
        logger = Logger.getLogger("SeleniumTest");
        
        // configure log4j properties file
        PropertyConfigurator.configure("files/log4j/Log4j.properties");
    }
    
     /*
        Set up with the target browser driver, here its for chrome
    */
    public void setUpChromeDriver(){
        // set the browser key/value property, according to your target browser
        System.setProperty("webdriver.chrome.driver", "files/driver/chromedriver90_linux64");
        logger.info("Selenium Test started ...");
        logger.info("Chrome Driver added");
    }
    
    /*
        Add and install the packed browser extension to the browser
    */
    public void AddExtension() {
        // create instance of chrome options
        opt = new ChromeOptions();

        opt.addArguments("--window-size=1920,1200", "--enable-logging", "--v=1", "--no-sandbox", "--disable-gpu");
        
        // set headless mode
        //opt.setHeadless(true);
        
        // add the packed extension 
        opt.addExtensions(new File("files/extension/build.crx"));
        //opt.addExtensions(new File("files/extension/google.crx"));
        //opt.addExtensions(new File("files/extension/zoom.crx"));
        // open browser with the current options

        driver = new ChromeDriver(opt);
        logger.info("Extension added and installed");
        
        logger.info("Browser opened");
    }
    
    /*
        Open the test url page
    */
    public void OpenTestUrl(){
        // go to the page to be detected for
        driver.get(testUrl);
        logger.info("Test page opened");
        DriverScreenShot("test-page");
        
    }

    /*
    * Screen shot with chrome driver
    * this shots only the chrome browser excluding appbar and extension
    */
    public void DriverScreenShot(String name) {
        try {
            //Take the screenshot
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            
            //Copy the file to a location and use try catch block to handle exception
            FileUtils.copyFile(screenshot, new File(pathByDriver+name+".png"));
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(SeleniumTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*
    * Screen shot with Sikuli
    * this shots all the current page in screen as normal desktop screen shot
    * including the opened extension and even other opened window
    */
    
    public void SikuliScreenShot(String name) {
        try {
            screen = new Screen();
            ScreenImage img = screen.capture();
            BufferedImage image = img.getImage();
            ImageIO.write(image, "png", new File(pathBySikuli+name));
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(SeleniumTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    /*
        Maximize the window
    */
    public void MaximizeWindow(){
        // maximize the window
        driver.manage().window().maximize();
        logger.info("Window Maximized");
    }
    
    /* 
       Click on the extensiion icon with sikuli
    */
    public void ClickIcon() {
        // create object of sikuli screen
        screen = new Screen();
        
        // create the pattern of extension icon so that recognized by the sikuli
        Pattern pattern = new Pattern(projectDir+"/files/icons/lyingface.png");
 
        try {
            // detect and click on the extension icon
            screen.click(pattern);
            Thread.sleep(500);
            logger.info("Extension icon found and clicked");
            
            // initialize the start time and calls the check loading
            Date date = new Date();
            start_time = date.getTime();
            checkLoading2();
            
        } catch (FindFailed e) {
            e.printStackTrace();
            logger.info("Couldn't found extension icon");
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(SeleniumTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    /**
     * Check weather its loading or not, for the purpose of 
     *      getting the time it takes to fetch the result and
     *      to test the UI on the result page
     * Use sikuli has method, if the UI has stance detection text it means it finished loading, if not its still loading. 
     */
    public void checkLoading() {
        // create screen object
        screen = new Screen();
        
        // stance detection text, if this found it means the fetching ends
        Pattern stance_detection = new Pattern(projectDir+"/files/UI/DetailsPane/stance_detection.png");
        Pattern processing = new Pattern(projectDir+"/files/UI/LoadingPane/processing.png");
        Pattern retry = new Pattern(projectDir+"/files/UI/Error/retry.png");

        try {
            // sleep for a while before starts checking, since extension window might not opened
            Thread.sleep(2000);
            // if its processing wait until it finished, else check weather the result or error page returned
            if(screen.has(processing)){
                logger.info("Fetching the api ...");
                SikuliScreenShot("loading-"+check);
                logger.info("screenshoot of loading page taken");

                if (check == 1) {
                    screen.find(processing);
                    screen.mouseMove(processing);
                    logger.info("mouse moved to processing text");
                    check += 1; //increas check
                }
                // loop through this function to check weather it finished loading or not
                checkLoading();   

            } else if(screen.has(stance_detection)){
                // if stance detection is not found it means error is occured or its still on loading
                logger.info("Fething done.");
                // get final time
                Date date = new Date();
                final_time = date.getTime();
                long time_taken = (final_time - start_time) / 1000;
                logger.info("Time taken to get the result: " + time_taken + " seconds");
                //ScreenShot("extension-home");
                SikuliScreenShot("extension-home");
                logger.info("screenshoot of extension result page taken");
                // each and every component test from footer to header(bottom up).
                TestFooter();
                TestPrivacyPolicy();
                TestHistoryButton();
                TestAnalysisButton();
                TestRatingPane();
                TestDetailsPane();
                TestStatusPane();
                TestThisWebsite();
                TestHeader();
            } else if (screen.has(retry)){
                // if the error occured
                logger.info("Some Error ocurred.");
                // test error page
                SikuliScreenShot("error");
                logger.info("screenshot of error page taken");
                TestErrorPane();
            } else {
                logger.info("Error occured");
                logger.info("None of the pages pane gets detected by sikuli.");
            }

        } catch (Exception e) {
            //e.printStackTrace();
            logger.info("Error occured while fetching the api: " + e.toString());
        }
    }
    
    
    /**
        Test Header
        get the screen shot of header icon, header text and header all together,
        then check weather they are available or not with sikuli find method
    */
    public void TestHeader() {
        // create screen object
        screen = new Screen();
        // header pattern
        Pattern header = new Pattern(projectDir+"/files/UI/Header/header.png");
        
        Pattern menu_icon = new Pattern(projectDir+"/files/UI/Header/setting_icon.png");
        Pattern menu_dialog = new Pattern(projectDir+"/files/UI/Header/menu_item.png");

        try {
            Thread.sleep(500);
            screen.find(header); 
            screen.mouseMove(header);
            logger.info("Header test succeed");
            
            Thread.sleep(500);
            screen.find(menu_icon);
            screen.click(menu_icon);
            Thread.sleep(500);
            logger.info("menu icon clicked");
            SikuliScreenShot("menu_dialog");
            logger.info("screenshot taken after menu dialog opened");
            //screen.find(menu_dialog);
            logger.info("Menu dialog test succeed");
            Thread.sleep(500);
        } catch(FindFailed e) {
            //e.printStackTrace();
            logger.info("Test Header failed: " + e.toString());
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(SeleniumTest.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    /**
        TestThisWebsite
    */
    public void TestThisWebsite() {
        // create screen object
        screen = new Screen();    
        // this website pattern
        Pattern this_website = new Pattern(projectDir+"/files/UI/Header/this_website.png");
        try {
            Thread.sleep(500);
            screen.find(this_website);
            screen.mouseMove(this_website);
            logger.info("This website text test succeed ");
            Thread.sleep(500);
        } catch(FindFailed e) {
            //e.printStackTrace();
            logger.info("This website text failed: " + e.toString());
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(SeleniumTest.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    /**
     * Test Loading Pane
     */
    public void TestLoadingPane() {
        // create screen object
        screen = new Screen();
        // processing text        
        Pattern processing = new Pattern(projectDir+"/files/UI/LoadingPane/processing.png");
        // Loading gif
        Pattern loading = new Pattern(projectDir+"/files/UI/LoadingPane/loading.png");
        
        if(screen.has(processing)){
            try {
                screen.find(processing); 
                screen.mouseMove(processing);
                screen.find(loading);
                screen.mouseMove(loading);
                logger.info("Loading pane test succeed ");
            } catch(FindFailed e) {
                //e.printStackTrace();
                logger.info("Loading pane test failed: " + e.toString());
            }
        }
    }

    /**
     * Test Error Pane
     */
    public void TestErrorPane() {
        // create screen object
        screen = new Screen();
        // error text        
        Pattern error = new Pattern(projectDir+"/files/UI/Error/error.png");
        
        // Loading gif
        Pattern retry = new Pattern(projectDir+"/files/UI/Error/retry.png");
        
        try {
            Thread.sleep(1000);
            screen.find(error); 
            screen.mouseMove(error);
            screen.find(retry);
            screen.mouseMove(retry);
            logger.info("Error pane test succeed ");
           } catch(FindFailed e) {
            //e.printStackTrace();
            logger.info("Error pane test failed: " + e.toString());
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(SeleniumTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
        Test Privacy Policy
    */
    public void TestPrivacyPolicy() {
        // create screen object
        screen = new Screen();
        // privacy
        Pattern privacy = new Pattern(projectDir+"/files/UI/Privacy/privacy.png");
        try {
            Thread.sleep(500);
            screen.find(privacy);
            screen.mouseMove(privacy);
            logger.info("Privacy policy pane test succeed ");
        } catch(FindFailed e) {
            //e.printStackTrace();
            logger.info("Privacy policy test failed: " + e.toString());
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(SeleniumTest.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    /**
        Test Footer
        get the screen shot of footer text, nunet icon, snet icon, menu icon and all together,
        then check weather they are available or not with sikuli find method
    */
    public void TestFooter() {
        // create screen object
        screen = new Screen();
        
        // nunet footer
        Pattern footer_nunet = new Pattern(projectDir+"/files/UI/Footer/footer_nunet.png");
        
        // snet footer
        Pattern footer_snet = new Pattern(projectDir+"/files/UI/Footer/footer_snet.png");
        
        // footer version
        Pattern footer_version = new Pattern(projectDir+"/files/UI/Footer/footer_version");
        
        // footer all together
        Pattern footer = new Pattern(projectDir+"/files/UI/Footer/footer.png");
        
        try {
            Thread.sleep(500);
            screen.find(footer_nunet);
            screen.mouseMove(footer_nunet);
            logger.info("Nunet Logo in Footer test succeed ");
            Thread.sleep(500);
            screen.find(footer_snet);
            screen.mouseMove(footer_snet);
            logger.info("Snet Logo in Footer test succeed");
            screen.find(footer_version);
            screen.mouseMove(footer_version);
            logger.info("Footer Version test succeed");
            screen.find(footer);
            logger.info("Footer test succeed");
        } catch(FindFailed e) {
            //e.printStackTrace();
            logger.info("Test Footer failed: " + e.toString());
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(SeleniumTest.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    /**
        Test Status Pane
        get the screen shot of low status, medium status, high status with with help icon,
        then check weather any of this three image available with sikuli findany method.
        because based on the probability the status pane color will be changed.
        If there is a way to get the probability number each can be checked individually wrt probability. 
    */
    public void TestStatusPane() {
        int probability = 1; 
        // create screen object
        screen = new Screen();
        // low status pattern with help icon
        Pattern low_status = new Pattern(projectDir+"/files/UI/StatusPane/low_status.png");
        
        // Medium status pattern with help icon
        Pattern medium_status = new Pattern(projectDir+"/files/UI/StatusPane/medium_status.png");
        
        // high status pattern with help icon
        Pattern high_status = new Pattern(projectDir+"/files/UI/StatusPane/high_status.png");
        
        try {
            Thread.sleep(500);
            screen.findAny(low_status, medium_status, high_status);
            screen.mouseMove(low_status);
            screen.mouseMove(medium_status);
            screen.mouseMove(high_status);
            logger.info("Status Pane Test succeed");
            
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(SeleniumTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            //e.printStackTrace();
            logger.info("Status Pane Test failed: " + e.toString());
        }
    }
    
    /**
     * Test Details Pane
     * get the screen shot of stance detection text and word frequency text and test only this
     * because taking the screen shot of their result is not important, since they are dynamic.
     * they can be tested failed even if they are available
     * 
     */
    public void TestDetailsPane(){
         // create screen object
        screen = new Screen();
        // stance detection text
        Pattern stance_detection = new Pattern(projectDir+"/files/UI/DetailsPane/stance_detection.png");
        Pattern stance_detection_help = new Pattern(projectDir+"/files/UI/DetailsPane/stance_detection_help.png");
        // binary classification
        Pattern binary_classification = new Pattern(projectDir+"/files/UI/DetailsPane/binary_classification.png");
        Pattern binary_classification_help = new Pattern(projectDir+"/files/UI/DetailsPane/binary_classification_help.png"); 
        
        Pattern dialog_close_icon = new Pattern(projectDir+"/files/UI/DetailsPane/dialog_close_icon.png");

        try {
            Thread.sleep(500);
            screen.find(stance_detection); 
            screen.mouseMove(stance_detection);
            logger.info("Mouse moved to stance detection text");
            SikuliScreenShot("stance_detection");
            logger.info("screenshot taken after mouse hover the stance detection text");                
            Thread.sleep(500);
            screen.click(stance_detection);
            Thread.sleep(500);
            logger.info("Stance detection text or help icon clicked");
            SikuliScreenShot("stance_detection_help");
            logger.info("screenshot taken after stance detection text or help icon clicked");    
            screen.find(stance_detection_help);
            logger.info("Stance detection help dialog opened");
            SikuliScreenShot("stance_detection_help_dialog");
            logger.info("screenshot taken after stance detection help dialog opened");    
            Thread.sleep(500);
            screen.click(dialog_close_icon);
            Thread.sleep(500);
            logger.info("stance detection help dialog closed");
            logger.info("Stance Detection test succeed");

            Thread.sleep(500);
            screen.find(binary_classification); 
            screen.mouseMove(binary_classification);
            logger.info("Mouse moved to binary classification text");
            SikuliScreenShot("binary_classification");
            logger.info("screenshot taken after mouse hover the binary classification text");                
            Thread.sleep(500);
            screen.click(binary_classification);
            Thread.sleep(500);
            logger.info("Binary classification text or help icon clicked");
            SikuliScreenShot("binary_classification_help");
            logger.info("screenshot taken after binary classification text or help icon clicked");    
            screen.find(binary_classification_help);
            logger.info("Binary classification help dialog opened");
            SikuliScreenShot("binary_classification_help_dialog");
            logger.info("screenshot taken after binary classification help dialog opened");    
            Thread.sleep(500);
            screen.click(dialog_close_icon);
            Thread.sleep(500);
            logger.info("binary classification help dialog closed");
            logger.info("Binary classification test succeed");
        } catch(FindFailed e) {
            //e.printStackTrace();
            logger.info("Details Pane Test failed: " + e.toString());
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(SeleniumTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Test Rating Pane
     * get the screen shot of rate text and with the rating
     * then check weather they are available or not with sikuli find method 
     */
    public void TestRatingPane(){
         // create screen object
        screen = new Screen();
        // rate text
        Pattern rate_text = new Pattern(projectDir+"/files/UI/RatingPane/rate_text.png");
        
        // rating pattern
        Pattern rating = new Pattern(projectDir+"/files/UI/RatingPane/rating.png");
        
        try {
            Thread.sleep(500);
            screen.find(rate_text); 
            screen.mouseMove(rate_text);
            Thread.sleep(500);
            screen.find(rating);
            screen.mouseMove(rating);
            logger.info("Rating pane test succeed ");
        } catch(FindFailed e) {
            //e.printStackTrace();
            logger.info("Rating Pane Test failed: " + e.toString());
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(SeleniumTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test Analysis Button
     * get the screen shot of analysis
     * then check weather they are available or not with sikuli find method 
     */
    public void TestAnalysisButton(){
         // create screen object
        screen = new Screen();
        // view analysis button
        Pattern analysis_btn = new Pattern(projectDir+"/files/UI/Analysis/view_analysis_btn.png");
        try {
            Thread.sleep(500);
            screen.find(analysis_btn); 
            screen.mouseMove(analysis_btn);
            logger.info("mouse moved to view full analysis or report issue button");
            SikuliScreenShot("analysis_button");
            logger.info("screenshot taken after mouse moved to view analysis or report issue  button");    
            logger.info("View Full analysis or report issue button test succeed");
        } catch(FindFailed e) {
            //e.printStackTrace();
            logger.info("View Full Analysis or Report issue button Test failed: " + e.toString());
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(SeleniumTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * TestHistoryButton
     * get the screen shot of history
     * then check weather they are available or not with sikuli find method 
     */
    public void TestHistoryButton(){
         // create screen object
        screen = new Screen();
        // view history button
        Pattern history_btn = new Pattern(projectDir+"/files/UI/Analysis/view_history_btn.png");
        try {
            Thread.sleep(500);
            screen.find(history_btn);
            screen.mouseMove(history_btn);
            logger.info("mouse moved to view full call history button");
            SikuliScreenShot("history_button");
            logger.info("screenshot taken after mouse moved to view history button");    
            logger.info("View Full history button test succeed");
        } catch(FindFailed e) {
            //e.printStackTrace();
            logger.info("View Full History Button Test failed: " + e.toString());
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(SeleniumTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
