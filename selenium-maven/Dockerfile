From ubuntu:18.04
# From ubuntu:latest

# Install OpenJDK-8
RUN apt-get update && \
    apt-get install -y openjdk-8-jdk && \
    apt-get install -y ant && \
    apt-get clean;


# Install Apache Maven

# first Install wget command to download maven
RUN apt-get update && \
    apt-get install -y wget && \
    rm -rf /var/lib/apt/lists/*

# then install maven
RUN cd /opt/ && \
    wget https://downloads.apache.org/maven/maven-3/3.8.1/binaries/apache-maven-3.8.1-bin.tar.gz && \ 
    tar -xf apache-maven-3.8.1-bin.tar.gz && \ 
    pwd && \
    mv apache-maven-3.8.1/ apache-maven/

RUN update-alternatives --install /usr/bin/mvn maven /opt/apache-maven/bin/mvn 1001

# Configure Apache Maven environment
RUN cd /etc/profile.d/ && \
    echo "################################################### \n# Apache Maven Environment Variables \n# MAVEN_HOME for Maven 1 - M2_HOME for Maven 2 \n\
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/ \n\
export M2_HOME=/opt/apache-maven \n\
export MAVEN_HOME=/opt/apache-maven \n\
export PATH=${M2_HOME}/bin:${PATH} \n\
" >> maven.sh && \
    pwd && \
    chmod +x maven.sh && \
    /bin/bash -c "source maven.sh"

RUN mvn --version

RUN pwd

RUN apt-get update; apt-get clean

RUN apt-get update && apt-get install -y gnupg2

# Set the Chrome repo.
#RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
#    && echo "deb http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list
# Install Chrome.
#RUN apt-get update && apt-get -y install google-chrome-stable

# Install google chrome 90 that can align the the chrome driver 90 that found in the file
ARG CHROME_VERSION="90.0.4430.85-1"
RUN wget --no-verbose -O /tmp/chrome.deb https://dl.google.com/linux/chrome/deb/pool/main/g/google-chrome-stable/google-chrome-stable_${CHROME_VERSION}_amd64.deb \
  && apt install -y /tmp/chrome.deb \
  && rm /tmp/chrome.deb

RUN google-chrome --version

# install the packages required to run XServer on gitlab ci
RUN apt-get install -y xorg-dev libglu1-mesa libgl1-mesa-dev xvfb libxinerama1 libxcursor1

# install libcaberra gtk module
RUN apt-get install libcanberra-gtk-module -y
RUN apt-get install libcanberra-gtk-module libcanberra-gtk3-module -y

# install nano and vim
RUN apt-get update
RUN apt-get install vim nano -y

# Set working directory
WORKDIR /selenium-maven

# copy the files
COPY . .

# make sure that Xvfb starts every time the box/vm is booted
#RUN Xvfb -ac :10.0 -screen 0 1280x1024x16 & 

# install the maven dependency
RUN mvn install

# write the command going to run the maven while docker run
# the first command is that makes to run the X Server on the gitlab ci
CMD xvfb-run -a -s "-screen 0 1400x900x24 +extension RANDR" -- mvn exec:java -D exec.mainClass="com.nunet.selenium.maven.MainClass" -Dexec.cleanupDaemonThreads=false
