# SampleApp-Java-Payroll-V4
SampleApp-Java-Payroll-V4

<p>Welcome to the Intuit Developer's Java Sample App for V4 Payroll API operations.</p>
<p>This sample app is meant to provide working examples of how to use V4 Payroll API using GraphQL and parse the response back.</p>  

## Requirements

In order to successfully run this sample app you need a few things:

1. Java 1.8
2. A [developer.intuit.com](http://developer.intuit.com) account using OAuth2.
3. An app on [developer.intuit.com](http://developer.intuit.com) using Accounting API and the associated client id, and client secret.
4. Generate tokens using [OAuth playground](https://developer.intuit.com/v2/ui#/playground)
 

## Running the code

This app is directed to provide individual sample code for querying various entities.
Each class has a main method that can be run individually.

Steps described below is to run the class for querying EmployerCompensation in Eclipse IDE.

1. Go to EmployerCompensation.java in package com.intuit.payroll
2. Add the accessToken value generated using playground.
3. Right click the file and Run as Java application
4. On the console you'll see the log being generated with the charge id.

Follow similar steps for other classes.

