package com.portfolio.backend.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Service
public class GoogleSheetsService {

    @Value("${sheet.id}")
    private String spreadsheetId;

    @Value("${google.credentials.json}")
    private String credentialsJson;

    public void saveContactToSheet(String name, String email, String subject, String message) {
        try {
            // Use environment variable instead of file
            if (credentialsJson == null || credentialsJson.isEmpty()) {
                throw new RuntimeException("Google credentials not found in environment variables");
            }

            // Convert JSON string to InputStream
            InputStream credentialsStream = new ByteArrayInputStream(credentialsJson.getBytes());

            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
                    .createScoped(Arrays.asList(SheetsScopes.SPREADSHEETS));

            // Create Sheets service
            Sheets service = new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName("Portfolio Backend")
                    .build();

            // Prepare data
            List<Object> rowData = Arrays.asList(
                    new java.util.Date().toString(),
                    name,
                    email,
                    subject,
                    message
            );

            List<List<Object>> values = Arrays.asList(rowData);
            ValueRange body = new ValueRange().setValues(values);

            // Append to sheet
            service.spreadsheets().values()
                    .append(spreadsheetId, "Sheet1!A:E", body)
                    .setValueInputOption("RAW")
                    .execute();

            System.out.println("✅ Contact saved to Google Sheets: " + email);

        } catch (Exception e) {
            System.err.println("❌ Error saving to Google Sheets: " + e.getMessage());
            throw new RuntimeException("Failed to save contact to sheet: " + e.getMessage());
        }
    }
}