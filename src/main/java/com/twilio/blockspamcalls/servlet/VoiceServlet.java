package com.twilio.blockspamcalls.servlet;

import com.twilio.twiml.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;


@WebServlet("/")
public class VoiceServlet extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        VoiceResponse.Builder voiceResponseBuilder = new VoiceResponse.Builder();
        boolean blockCall = false;

        String addOnsString = request.getParameter("AddOns");

        if(addOnsString != null && addOnsString != "") {
            JSONObject addOns = new JSONObject(addOnsString);
            if (addOns.optString("status").equals("successful") &&
                    addOns.has("results")) {
                JSONObject results = addOns.getJSONObject("results");
                blockCall = shouldBeBlockedByNomoRobo(results)
                        || shouldBeBlockedByWhitePages(results)
                        || shouldBeBlockedByMarchex(results);
            }
        }

        if (blockCall) {
            voiceResponseBuilder.reject(new Reject.Builder().build());
        } else {
            voiceResponseBuilder.say(new Say
                    .Builder("Welcome to the jungle.")
                    .build());
            voiceResponseBuilder.hangup(new Hangup());
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

    private boolean shouldBeBlockedByNomoRobo(JSONObject results) {
        if(!results.has("nomorobo_spamscore")) {
            return false;
        }

        JSONObject nomorobo = results.getJSONObject("nomorobo_spamscore");

        if (!nomorobo.optString("status").equals("successful")) {
            return false;
        }

        Integer score = getSafeJSONObject(nomorobo, "result")
                .optInt("score", -1);
        return score == 1;
    }

    private boolean shouldBeBlockedByWhitePages(JSONObject results) {
        if(!results.has("whitepages_pro_phone_rep")) {
            return false;
        }

        JSONObject whitePages = results.getJSONObject("whitepages_pro_phone_rep");
        if (!whitePages.optString("status").equals("successful")) {
            return false;
        }

        JSONObject whitePagesResult = getSafeJSONObject(whitePages, "result");
        JSONArray whitePagesResults = getSafeJSONArray(whitePagesResult, "results");

        Iterator<Object> resultsIterator = whitePagesResults.iterator();
        while(resultsIterator.hasNext()) {
            Object result = resultsIterator.next();
            if(!(result instanceof JSONObject)) continue;
            JSONObject reputation = ((JSONObject) result).getJSONObject("reputation");
            if (reputation != null &&
                    reputation.has("level") &&
                    reputation.getInt("level") == 4) {
                return true;
            }
        }

        return false;
    }

    private boolean shouldBeBlockedByMarchex(JSONObject results) {
        if(!results.has("marchex_cleancall")) {
            return false;
        }
        JSONObject cleanCall = results.getJSONObject("marchex_cleancall");
        if (!cleanCall.optString("status").equals("successful")) {
            return false;
        }

        JSONObject result = getSafeJSONObject(cleanCall, "result");
        JSONObject resultChild = getSafeJSONObject(result, "result");
        String recommendation = resultChild.optString("recommendation");

        return recommendation.equals("BLOCK");
    }

    private static JSONObject getSafeJSONObject(JSONObject parent, String key) {
        if(parent.has(key)) {
            return parent.getJSONObject(key);
        }
        return new JSONObject();
    }

    private static JSONArray getSafeJSONArray(JSONObject parent, String key) {
        if(parent.has(key)) {
            return parent.getJSONArray(key);
        }
        return new JSONArray();
    }
}
