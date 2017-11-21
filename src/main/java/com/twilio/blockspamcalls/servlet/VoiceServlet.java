package com.twilio.blockspamcalls.servlet;

import com.google.common.collect.ImmutableList;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ReadContext;
import com.twilio.twiml.TwiMLException;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Hangup;
import com.twilio.twiml.voice.Reject;
import com.twilio.twiml.voice.Say;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


@WebServlet("/")
public class VoiceServlet extends HttpServlet {

    private static final String WHITEPAGES_SPAM = "4";
    private static final String NOMOROBO_SPAM = "1";
    private static final List<String> RESULT_PATHS = ImmutableList.of(
            "$.results.marchex_cleancall.result.result.[?(@.recommendation!='PASS')]",
            "$.results.whitepages_pro_phone_rep.result.[?(@.reputation_level==" +
                    WHITEPAGES_SPAM + ")]",
            "$.results.nomorobo_spamscore.[?(@.status=='successful' && @.result.score==" +
                    NOMOROBO_SPAM + ")]"
    );

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/xml");

        VoiceResponse.Builder voiceResponseBuilder = new VoiceResponse.Builder();
        boolean blockCall = false;
        String addOnsString = request.getParameter("AddOns");

        if(isNotBlank(addOnsString)) {
            ReadContext addOns = parseAddOnsParameter(addOnsString);

            int i = 0;
            while(!blockCall && i < RESULT_PATHS.size()) {
                List result = addOns.read(RESULT_PATHS.get(i));
                blockCall = result.size() > 0;
                i++;
            }
        }

        if (blockCall) {
            voiceResponseBuilder.reject(new Reject.Builder().build());
        } else {
            voiceResponseBuilder.say(new Say
                    .Builder("Welcome to the jungle.")
                    .build());
            voiceResponseBuilder.hangup(new Hangup.Builder().build());
        }
        try {
            response
                    .getOutputStream()
                    .write(voiceResponseBuilder
                            .build()
                            .toXml()
                            .getBytes());
        } catch (TwiMLException e) {
            e.printStackTrace();
            PrintWriter writer = response.getWriter();
            writer.write("An error ocurred processing the POST request. " + e.getMessage());
        }
    }

    private boolean isNotBlank(String addOnsString) {
        return addOnsString != null && addOnsString != "";
    }

    private ReadContext parseAddOnsParameter(String addOnsString) {
        Configuration configuration = Configuration
                .builder()
                .options(Option.SUPPRESS_EXCEPTIONS)
                .build();

        return JsonPath.using(configuration).parse(addOnsString);
    }

}
