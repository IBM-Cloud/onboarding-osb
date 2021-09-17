FROM openjdk:8-jdk-alpine
RUN apk upgrade && apk add openjdk8-jre-lib=8.275.01-r0 && apk add openjdk8=8.275.01-r0

COPY target/osb-sdk.jar .

CMD java -jar osb-sdk.jar