# Guide for ui modifications and integration

## 1.Prerequisite 
- node 14.17.* or newer
- node 6.14.13 or newer
- `suggested: Lastest LTS node install`

</br>

>### Run all commands in client directory 

</br>

## 2. First steps
- Install dependencies
  
	``` npm install ```

## 3. Building project
- ```npm run build```

	This creates build for react application which will is used in java application.

	### Build java application again to test changes.
	> Run this inside project root folder
	- Build java application
  
		```mvn clean install```

	- Run java application locally
  
		```java -jar /target/osb-sdk.jar```