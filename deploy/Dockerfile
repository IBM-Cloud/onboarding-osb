FROM maven:3.8.2-jdk-8

RUN apt-get update

RUN apt-get install -y jq

RUN java -version

RUN echo "---------- Installing cloudfoundry cli ----------" \
 && wget -q -O - https://packages.cloudfoundry.org/debian/cli.cloudfoundry.org.key | apt-key add - \
 && echo "deb https://packages.cloudfoundry.org/debian stable main" | tee /etc/apt/sources.list.d/cloudfoundry-cli.list \
 && apt-get update && apt-get install -y --allow-unauthenticated cf-cli \
 && cf -v

RUN curl -sL https://raw.githubusercontent.com/IBM-Cloud/ibm-cloud-developer-tools/master/linux-installer/idt-installer | bash \
 && ibmcloud plugin install code-engine
