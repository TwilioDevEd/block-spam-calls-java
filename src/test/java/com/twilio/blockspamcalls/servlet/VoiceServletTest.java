package com.twilio.blockspamcalls.servlet;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VoiceServletTest {

    private VoiceServlet voiceServlet;

    @Before
    public void setUp() {
        voiceServlet = new VoiceServlet();
    }

    @Test
    public void testSuccessfulWithoutAddons() throws IOException {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getParameter("AddOns")).thenReturn(null);
        ServletOutputStream outputStream = new ByteArrayServletOutputStream();
        when(response.getOutputStream()).thenReturn(outputStream);

        // When
        voiceServlet.doPost(request, response);

        // Then
        assertFalse(outputStream.toString().contains("<Reject"));
    }

    @Test
    public void testSuccessfulWithMarchex() throws IOException {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        String addOns = getResource("/successful_marchex.json");
        when(request.getParameter("AddOns")).thenReturn(addOns);

        ByteArrayServletOutputStream outputStream = new ByteArrayServletOutputStream();
        when(response.getOutputStream()).thenReturn(outputStream);

        // When
        voiceServlet.doPost(request, response);

        // Then
        assertFalse(outputStream.getContent().contains("<Reject"));
    }

    @Test
    public void testBlockedWithMarchex() throws IOException {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        String addOns = getResource("/spam_marchex.json");
        when(request.getParameter("AddOns")).thenReturn(addOns);

        ByteArrayServletOutputStream outputStream = new ByteArrayServletOutputStream();
        when(response.getOutputStream()).thenReturn(outputStream);

        // When
        voiceServlet.doPost(request, response);

        // Then
        assertTrue(outputStream.getContent().contains("<Reject"));
    }

    @Test
    public void testSuccessfulWithNomorobo() throws IOException {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        String addOns = getResource("/successful_nomorobo.json");
        when(request.getParameter("AddOns")).thenReturn(addOns);

        ByteArrayServletOutputStream outputStream = new ByteArrayServletOutputStream();
        when(response.getOutputStream()).thenReturn(outputStream);

        // When
        voiceServlet.doPost(request, response);

        // Then
        assertFalse(outputStream.getContent().contains("<Reject"));
    }

    @Test
    public void testBlockedWithNomorobo() throws IOException {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        String addOns = getResource("/spam_nomorobo.json");
        when(request.getParameter("AddOns")).thenReturn(addOns);

        ByteArrayServletOutputStream outputStream = new ByteArrayServletOutputStream();
        when(response.getOutputStream()).thenReturn(outputStream);

        // When
        voiceServlet.doPost(request, response);

        // Then
        assertTrue(outputStream.getContent().contains("<Reject"));
    }

    @Test
    public void testSuccessfulWithWhitePages() throws IOException {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        String addOns = getResource("/successful_whitepages.json");
        when(request.getParameter("AddOns")).thenReturn(addOns);

        ByteArrayServletOutputStream outputStream = new ByteArrayServletOutputStream();
        when(response.getOutputStream()).thenReturn(outputStream);

        // When
        voiceServlet.doPost(request, response);

        // Then
        assertFalse(outputStream.getContent().contains("<Reject"));
    }

    @Test
    public void testBlockedWithWhitePages() throws IOException {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        String addOns = getResource("/spam_whitepages.json");
        when(request.getParameter("AddOns")).thenReturn(addOns);

        ByteArrayServletOutputStream outputStream = new ByteArrayServletOutputStream();
        when(response.getOutputStream()).thenReturn(outputStream);

        // When
        voiceServlet.doPost(request, response);

        // Then
        assertTrue(outputStream.getContent().contains("<Reject"));
    }

    @Test
    public void testSuccessfulWithNomoRoboApiFailure() throws IOException {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        String s = "/failed_nomorobo.json";
        String addOns = getResource(s);
        when(request.getParameter("AddOns")).thenReturn(addOns);

        ByteArrayServletOutputStream outputStream = new ByteArrayServletOutputStream();
        when(response.getOutputStream()).thenReturn(outputStream);

        // When
        voiceServlet.doPost(request, response);

        // Then
        assertFalse(outputStream.getContent().contains("<Reject"));
    }

    private String getResource(String fileName) throws IOException {
        return IOUtils.toString(this.getClass().getResourceAsStream(fileName), "UTF-8");
    }

}

class ByteArrayServletOutputStream extends ServletOutputStream {
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    public void write(int i) throws IOException {
        baos.write(i);
    }
    public String getContent() throws UnsupportedEncodingException {
        return baos.toString("UTF-8");
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {

    }
}