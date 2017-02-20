<a href="https://www.twilio.com">
  <img src="https://static0.twilio.com/marketing/bundles/marketing/img/logos/wordmark-red.svg" alt="Twilio" width="250" />
</a>

# Block Spam Calls. Powered by Twilio - Java/Servlets
[![Build Status](https://travis-ci.org/TwilioDevEd/block-spam-calls-java.svg?branch=master)](https://travis-ci.org/TwilioDevEd/block-spam-calls-java)

Learn how to use Twilio add-ons to block spam calls.

## Local development

To run the app locally, clone this repository and `cd` into its directory:

1. First clone this repository and `cd` into its directory:

   ```bash
   git clone git@github.com:TwilioDevEd/block-spam-calls-java.git

   cd block-spam-calls-java
   ```

1. Run the application using Maven.

   ```bash
   $ mvn compile && mvn jetty:run
   ```

   This will run the embedded Jetty application server that uses port 8080. You can change this value
   on the [app's main file](//github.com/TwilioDevEd/block-spam-calls-java/blob/master/src/main/java/com/twilio/blockspamcalls/App.java)

1. To actually forward incoming calls, your development server will need to be publicly accessible. [We recommend using ngrok to solve this problem](https://www.twilio.com/blog/2015/09/6-awesome-reasons-to-use-ngrok-when-testing-webhooks.html).

    Once you have started ngrok, update your TwiML app's voice URL setting to use your ngrok hostname, so it will look something like this:

    ```
    http://88b37ada.ngrok.io/
    ```

1. Check it out at [http://localhost:8080](http://localhost:8080)

That's it

## Run the tests

You can run the tests locally by typing

```bash
mvn test
```

## Meta

* No warranty expressed or implied. Software is as is. Diggity.
* [MIT License](http://www.opensource.org/licenses/mit-license.html)
* Lovingly crafted by Twilio Developer Education.
