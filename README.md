# selenium-tests

Testing the browser extension with selenium java. 


1. The extension should be packed

To pack the extension
 
    a. Go to chrome://extensions/
    b. Enable the developer mode
    c. Click the Pack Extension button
    d. Browse the unpacked extension directory
    e. Choose the directory where the packed extension to be saved or just leave empty to save it in the directory where the unpacked extension found
    f. Then click the Pack Extension button

2. See the following comment on how to run selenium tests on ubuntu: https://gitlab.com/nunet/fake-news-detection/selenium-tests/-/issues/8#note_551279255 



## Testing with Maven

1. Install java 8 

    ```
    $ sudo apt update
    $ sudo apt install openjdk-8-jdk
    ```

2. Install apache maven 
    ```
    $ cd /opt/
    $ sudo wget https://downloads.apache.org/maven/maven-3/3.8.1/binaries/apache-maven-3.8.1-bin.tar.gz
    $ sudo tar -xf apache-maven-3.8.1-bin.tar.gz
    $ sudo mv apache-maven-3.8.1/ apache-maven/

    $ sudo update-alternatives --install /usr/bin/mvn maven /opt/apache-maven/bin/mvn 1001

    $ mvn --version
    $ sudo rm apache-maven-3.8.1-bin.tar.gz
    ```

3. Configure Apache Maven environment

    Go to /etc/profile.d/ then create maven.sh file and paste the ff script on it
    ```
    $ cd /etc/profile.d/
    $ sudo gedit maven.sh

    ###################################################
    # Apache Maven Environment Variables
    # MAVEN_HOME for Maven 1 - M2_HOME for Maven 2
    export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
    export M2_HOME=/opt/apache-maven
    export MAVEN_HOME=/opt/apache-maven
    export PATH=${M2_HOME}/bin:${PATH}
    ```

    make the bash script to be executable
    ```
    $ sudo chmod +x maven.sh
    $ sudo -s source maven.sh
    ```

4. Install google chrome current stable

    ```
    $ cd /opt/
    $ sudo wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb 
    $ sudo apt install ./google-chrome-stable_current_amd64.deb -y
    $ sudo rm google-chrome-stable_current_amd64.deb
    ```

5. Clone the project
    ```
    $ git clone https://gitlab.com/nunet/fake-news-detection/selenium-tests.git
    ```

6. Install the dependency
    ```
    $ cd selenium-tests/selenium-maven/
    $ mvn install 
    ```

7. Run the Test
    ```
    $ cd selenium-tests/selenium-maven/
    $ mvn exec:java -D exec.mainClass="com.nunet.selenium.maven.MainClass"
    ```