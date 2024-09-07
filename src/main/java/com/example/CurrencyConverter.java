package com.example;

import org.json.JSONObject;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class CurrencyConverter {

    private static final String API_KEY = "your_api_key_here"; // Replace with your API key
    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/"; // Replace with your API URL

    // Fetch exchange rates from the API
    private static JSONObject fetchExchangeRates(String baseCurrency) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            String urlStr = API_URL + baseCurrency + "?apiKey=" + API_KEY;
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                return new JSONObject(response.toString());
            } else {
                System.out.println("Failed to retrieve exchange rates. Response code: " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("Error during HTTP request: " + e.getMessage());
        } finally {
            try {
                if (reader != null) reader.close();
                if (connection != null) connection.disconnect();
            } catch (Exception e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        return null;
    }

    // Convert currency using the rates JSON object
    private static double convertCurrency(double amount, String baseCurrency, String targetCurrency, JSONObject rates) {
        if (rates == null) {
            System.out.println("Failed to retrieve exchange rates");
            return 0.0;
        }

        try {
            JSONObject ratesObj = rates.getJSONObject("rates");
            double baseRate = ratesObj.getDouble(baseCurrency);
            double targetRate = ratesObj.getDouble(targetCurrency);

            double amountInBaseCurrency = amount / baseRate;
            return amountInBaseCurrency * targetRate;
        } catch (JSONException e) {
            System.out.println("Error during currency conversion: " + e.getMessage());
        }
        return 0.0;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter the amount: ");
            double amount = scanner.nextDouble();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter base currency (e.g., USD, EUR): ");
            String baseCurrency = scanner.nextLine().toUpperCase();

            System.out.print("Enter target currency (e.g., USD, EUR): ");
            String targetCurrency = scanner.nextLine().toUpperCase();

            // Fetch exchange rates
            JSONObject rates = fetchExchangeRates(baseCurrency);

            // Convert currency
            double convertedAmount = convertCurrency(amount, baseCurrency, targetCurrency, rates);

            // Display result
            System.out.printf("Converted amount: %.2f %s%n", convertedAmount, targetCurrency);
        } finally {
            scanner.close();
        }
    }
}
