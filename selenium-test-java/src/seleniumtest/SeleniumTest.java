/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seleniumtest;

import java.io.File;
import java.util.Date;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

/**
 *
 * @author admin
 */
public class SeleniumTest {
    
    ChromeDriver driver;
    ChromeOptions opt;
    Logger logger;
    
    Screen screen; // Sikuli screen object
    
    String testUrl = "https://www.bbc.com/";
    
    long start_time; // time when fetching started
    long final_time; // time when fetching end
    int check = 1; // value used in check Loading to prevent displaying log again and again while looping
    
    // Temporar window and linux absolute path, until the relative path problem with sikuli gets solved
    String winPath = "C:\\Users\\user\\Documents\\iCOG\\Nunet\\TestPane\\selenium-tests\\selenium-test-java\\files\\";
    String linuxPath = "/home/buli/Documents/iCog/Nunet/Nunet-Dev/selenium-tests/selenium-test-java/files/";
    
    /*
        set up the logger with Log4j library
    */
    public void setUpLogger(){
        // create logger instance with current class name 
        logger = Logger.getLogger("SeleniumTest");
        
        // configure log4j properties file
        PropertyConfigurator.configure("src/file/Log4j.properties");
        
    }
    
    /*
        Set up with the target browser driver, here its for chrome
    */
    public void setUpChromeDriver(){
        // set the browser key/value property, according to your target browser
        //System.setProperty("webdriver.chrome.driver", "src/ChromeDriver/chromedriver_linux64");
        System.setProperty("webdriver.chrome.driver", "src/ChromeDriver/chromedriver_win64.exe");
        logger.info("Selenium Test started ..");
        logger.info("Chrome Driver added");
    }
    
    /*
        Add and install the packed browser extension to the browser
    */
    public void AddExtension() {
        // create instance of chrome options
        opt = new ChromeOptions();
        
        // add the packed extension 
        opt.addExtensions(new File("src/file/build.crx"));
        logger.info("Extension added and installed");
        
        // open browser with the current options
        driver = new ChromeDriver(opt);
        logger.info("Browser opened");
    }
    
    /*
        Open the test url page
    */
    public void OpenTestUrl(){
        // go to the page to be detected for
        driver.get(testUrl);
        logger.info("Test page opened");
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
        //Pattern pattern = new Pattern(linuxPath+"extension-icon.PNG");
        //Pattern pattern = new Pattern("src/file/extension-icon.PNG");
        Pattern pattern = new Pattern(winPath+"extension-icon.PNG");

        try {
            // detect and click on the extension icon
            screen.click(pattern);
            logger.info("Extension icon found and clicked");
            
            // initialize the start time and calls the check loading
            Date date = new Date();
            start_time = date.getTime();
            checkLoading();
            
        } catch (FindFailed e) {
            e.printStackTrace();
            logger.info("Couldn't found extension icon");
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
        Pattern stance_detection_text = new Pattern(winPath+"UI\\DetailsPane\\stance_detection_text.PNG");
        //Pattern stance_detection_text = new Pattern(linuxPath+"UI/DetailsPane/stance_detection_text.PNG");
        
        try {
            boolean found = screen.has(stance_detection_text);
            if(!found) {
                if(check == 1) {
                    logger.info("Fetching started ...");
                    // test loading pane here while its loading
                    TestLoadingPane();
                    check = check+1;
                }
                // if its loading loop through again and again
                checkLoading();
            }
            else {
                logger.info("Fething done.");
                
                // get final time
                Date date = new Date();
                final_time = date.getTime();
                long time_taken = (final_time - start_time) / 1000;
                logger.info("Time taken to get the result: " + time_taken + " seconds");
                TestHeader();
                TestFooter();
                TestMenuPane();
                TestStatusPane();
                TestDetailsPane();
                TestRatingPane();
            }              
        } catch (Exception e) {
            //e.printStackTrace();
            logger.info("Error occured while fetching: " + e.toString());
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
        // header icon pattern
        Pattern header_icon = new Pattern(winPath+"UI\\Header\\header_icon.PNG");
        //Pattern header_icon = new Pattern(linuxPath+"UI/Header/header_icon.PNG");
        
        // header text pattern
        Pattern header_text = new Pattern(winPath+"UI\\Header\\header_text.PNG");
        //Pattern pattern = new Pattern(linuxPath+"UI/Header/header_text.PNG");
        
        // header all together
        Pattern header = new Pattern(winPath+"UI\\Header\\header.PNG");
        //Pattern pattern = new Pattern(linuxPath+"UI/Header/header.PNG");
        try {
            Thread.sleep(2000);
            screen.find(header_icon); 
            logger.info("Header icon test succeed");
            Thread.sleep(1000);
            screen.find(header_text);
            logger.info("Header text test succeed ");
            Thread.sleep(1000);
            screen.find(header);
            logger.info("Test header succeed");
        } catch(FindFailed e) {
            //e.printStackTrace();
            logger.info("Test Header failed: " + e.toString());
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
        // please wait text
        
        Pattern pleasewait  = new Pattern(winPath+"UI\\LoadingPane\\pleasewait.PNG");
        //Pattern pleasewait = new Pattern(linuxPath+"UI/LoadingPane/pleasewait.PNG");
        
        // Loading gif
        Pattern loading = new Pattern(winPath+"UI\\LoadingPane\\loading.PNG");
        //Pattern loading = new Pattern(linuxPath+"UI/LoadingPane/loading.PNG");
        
        try {
            Thread.sleep(1000);
            screen.find(pleasewait); 
            screen.find(loading);
            logger.info("Loading pane test succeed ");
           } catch(FindFailed e) {
            //e.printStackTrace();
            logger.info("Loading pane test failed: " + e.toString());
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
  
        // footer text
        Pattern footer_text = new Pattern(winPath+"UI\\Footer\\footer_text.PNG");
        //Pattern header_icon = new Pattern(linuxPath+"UI/Footer/footer_text.PNG");
        
        // nunet icon
        Pattern nunet_icon = new Pattern(winPath+"UI\\Footer\\nunet_icon.PNG");
        //Pattern pattern = new Pattern(linuxPath+"UI/Footer/nunet_icon.PNG");
        
        // snet icon
        Pattern snet_icon = new Pattern(winPath+"UI\\Footer\\snet_icon.PNG");
        //Pattern pattern = new Pattern(linuxPath+"UI/Footer/snet_icon.PNG");
        
        // menu icon
        Pattern menu_icon = new Pattern(winPath+"UI\\Footer\\menu_icon.PNG");
        //Pattern pattern = new Pattern(linuxPath+"UI/Footer/menu_icon.PNG");
        
        // footer all together
        Pattern footer = new Pattern(winPath+"UI\\Footer\\footer.PNG");
        //Pattern pattern = new Pattern(linuxPath+"UI/Footer/footer.PNG");
        
        try {
            Thread.sleep(1000);
            screen.find(footer_text); 
            logger.info("Footer text test succeed");
            Thread.sleep(1000);
            screen.find(nunet_icon);
            logger.info("Footer Nunet icon test succeed ");
            Thread.sleep(1000);
            screen.find(snet_icon);
            logger.info("Footer Snet icon test succeed");
            screen.find(menu_icon);
            logger.info("Footer menu icon test succeed");
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
     * Test Menu Pane
     * capture the screen shot of feedback icon, history icon, legalterms icon, about icon and all together,
       then check weather they are available or not with sikuli find method
    */
    public void TestMenuPane() {
         // create screen object
        screen = new Screen();
  
        // feedback menu
        Pattern feedback_menu = new Pattern(winPath+"UI\\MenuPane\\feedback.PNG");
        //Pattern feedback_menu = new Pattern(linuxPath+"UI/MenuPane/feedback.PNG");
        
        // history menu
        Pattern history_menu = new Pattern(winPath+"UI\\MenuPane\\history.PNG");
        //Pattern history_menu = new Pattern(linuxPath+"UI/MenuPane/history.PNG");
        
        // legal terms menu
        Pattern legal_menu = new Pattern(winPath+"UI\\MenuPane\\legalterms.PNG");
        //Pattern legal_menu = new Pattern(linuxPath+"UI/MenuPane/legalterms.PNG");
        
        // about menu
        Pattern about_menu = new Pattern(winPath+"UI\\MenuPane\\about.PNG");
        //Pattern about_menu = new Pattern(linuxPath+"UI/MenuPane/about.PNG");
        
        // menu pane all together
        Pattern menuPane = new Pattern(winPath+"UI\\MenuPane\\menupane.PNG");
        //Pattern menuPane = new Pattern(linuxPath+"UI/MenuPane/menupane.PNG");
        
        try {
            Thread.sleep(1000);
            screen.find(feedback_menu); 
            logger.info("Feedback Menu test succeed");
            Thread.sleep(1000);
            screen.find(history_menu);
            logger.info("History Menu test succeed ");
            Thread.sleep(1000);
            screen.find(legal_menu);
            logger.info("Legal Terms menu test succeed");
            screen.find(about_menu);
            logger.info("About Menu test succeed");
            screen.find(menuPane);
            logger.info("MenuPane test succeed");
        } catch(FindFailed e) {
            //e.printStackTrace();
            logger.info("Test Menu Pane failed: " + e.toString());
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
        Pattern low_status = new Pattern(winPath+"UI\\StatusPane\\low_status.PNG");
        //Pattern low_status = new Pattern(linuxPath+"UI/StatusPane/low_status.PNG");
        
        // Medium status pattern with help icon
        Pattern medium_status = new Pattern(winPath+"UI\\StatusPane\\medium_status.PNG");
        //Pattern medium_status = new Pattern(linuxPath+"UI/StatusPane/medium_status.PNG");
        
        // high status pattern with help icon
        Pattern high_status = new Pattern(winPath+"UI\\StatusPane\\high_status.PNG");
        //Pattern high_status = new Pattern(linuxPath+"UI/StatusPane/high_status.PNG");
        
        try {
            Thread.sleep(1000);
            screen.findAny(low_status, medium_status, high_status);
            logger.info("Status Pane Test succeed");
            
//            if(probability == 1) {
//                Thread.sleep(1000);
//                screen.find(low_status); 
//                logger.info("Status Low test succeed");
//            }
//            else if(probability == 2) {
//                Thread.sleep(1000);
//                screen.find(medium_status); 
//                logger.info("Status Medium test succeed");
//            }
//            else {
//                Thread.sleep(1000);
//                screen.find(high_status); 
//                logger.info("Status High test succeed");
//            }
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
        Pattern stance_detection_text = new Pattern(winPath+"UI\\DetailsPane\\stance_detection_text.PNG");
        //Pattern stance_detection_text = new Pattern(linuxPath+"UI/DetailsPane/stance_detection_text.PNG");
        
        // header text pattern
        Pattern wordfrequency_text = new Pattern(winPath+"UI\\DetailsPane\\wordfrequency_text.PNG");
        //Pattern wordfrequency_text = new Pattern(linuxPath+"UI/DetailsPane/wordfrequency_text.PNG");
        
        try {
            Thread.sleep(2000);
            screen.find(stance_detection_text); 
            logger.info("Stance Detection test succeed");
            Thread.sleep(1000);
            screen.find(wordfrequency_text);
            logger.info("WordFrquency test succeed ");
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
        Pattern rate_text = new Pattern(winPath+"UI\\RatingPane\\rate_text.PNG");
        //Pattern rate_text = new Pattern(linuxPath+"UI/RatingPane/rate_text.PNG");
        
        // rating pattern
        Pattern rating = new Pattern(winPath+"UI\\RatingPane\\rating.PNG");
        //Pattern rating = new Pattern(linuxPath+"UI/RatingPane/rating.PNG");
        
        try {
            Thread.sleep(1000);
            screen.find(rate_text); 
            Thread.sleep(1000);
            screen.find(rating);
            logger.info("Rating pane test succeed ");
        } catch(FindFailed e) {
            //e.printStackTrace();
            logger.info("Rating Pane Test failed: " + e.toString());
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(SeleniumTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // create instance of selenium Test class
        SeleniumTest st = new SeleniumTest();
        
        st.setUpLogger();
        st.setUpChromeDriver();
        st.AddExtension();
        //st.MaximizeWindow();
        st.OpenTestUrl();
        st.ClickIcon();
    }
}
