FROM maven:3.8.2-jdk-8

RUN apt-get update

RUN apt-get install -y jq

RUN java -version

RUN curl -sL https://raw.githubusercontent.com/IBM-Cloud/ibm-cloud-developer-tools/master/linux-installer/idt-installer | bash \
 && ibmcloud plugin install code-engine
