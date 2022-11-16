/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nunet.selenium.maven;


/**
 *
 * @author buli
 */
public class MainClass {
    
    public static void main(String[] args) {
        // create instance of selenium Test class
        SeleniumTest st = new SeleniumTest();
        
        st.setUpLogger();
        st.setUpChromeDriver();
        st.AddExtension();
        st.MaximizeWindow();
        st.OpenTestUrl();
        st.ClickIcon();
        
    }
}
