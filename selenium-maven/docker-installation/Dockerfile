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