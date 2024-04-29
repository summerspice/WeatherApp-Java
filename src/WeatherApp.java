//retrieve weather data from api

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class WeatherApp {
  // fetch weather
  public static JSONObject getWeatherData(String locationName) {
    JSONArray locationData = getLocationData(locationName);

    JSONObject location = (JSONObject) locationData.get(0);
    double latitude = (double) location.get("latitude");
    double longitude = (double) location.get("longitude");

    // build api result
    String urlString = "https://api.open-meteo.com/v1/forecast?" +
        "latitude=" + latitude + "&longitude=" + longitude +
        "&hourly=temperature_2m,relativehumidity_2m,weathercode,windspeed_10m&timezone=America%2FLos_Angeles";

    try {

      // call api and get response
      HttpURLConnection conn = fetchApiResponse(urlString);

      // check for resoponse status
      // status is 200?
      if (conn.getResponseCode() != 200) {
        System.out.println("Error: Could not connect to api");

        return null;
      }

      // store result json data
      StringBuilder resultJson = new StringBuilder();
      Scanner scanner = new Scanner(conn.getInputStream());
      while (scanner.hasNext()) {
        resultJson.append(scanner.nextLine());
      }

      scanner.close();

      conn.disconnect();

      // parse through data
      JSONParser parser = new JSONParser();
      JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

      JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

      JSONArray time = (JSONArray) hourly.get("time");
      int index = findIndexOfCurrentTime(time);

      JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
      double temperature = (double) temperatureData.get(index);

      JSONArray weathercode = (JSONArray) hourly.get("weathercode");
      String weatherCondition = convertWeatherCode((long) weathercode.get(index));

      JSONArray relativeHumidity = (JSONArray) hourly.get("relativehumidity_2m");
      long humidity = (long) relativeHumidity.get(index);

      JSONArray windspeedData = (JSONArray) hourly.get("windspeed_10m");
      double windspeed = (double) windspeedData.get(index);

      // build the weather json data object that we are going to access
      JSONObject weatherData = new JSONObject();
      weatherData.put("temperature", temperature);
      weatherData.put("weather_condition", weatherCondition);
      weatherData.put("humidity", humidity);
      weatherData.put("windspeed", windspeed);

      return weatherData;

    } catch (Exception e) {
      e.printStackTrace();

    }

    return null;

  }

  static JSONArray getLocationData(String locationName) {
    locationName = locationName.replaceAll(" ", "+");

    // build API url with location parameter
    // https://geocoding-api.open-meteo.com/v1/search?name=Berlin&count=10&language=en&format=json
    String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + locationName
        + "&count=10&language=en&format=json";

    try {

      HttpURLConnection conn = fetchApiResponse(urlString);

      if (conn.getResponseCode() != 200) {
        System.out.println("Error, could not connect to API server");
        return null;

      } else {
        StringBuilder resultJson = new StringBuilder();
        Scanner scanner = new Scanner(conn.getInputStream());
        while (scanner.hasNext()) {
          resultJson.append(scanner.nextLine());
        }

        scanner.close();

        conn.disconnect();

        JSONParser parser = new JSONParser();
        JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

        JSONArray locationData = (JSONArray) resultsJsonObj.get("results");

        return locationData;

      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    // couldn't find location
    return null;
  }

  private static HttpURLConnection fetchApiResponse(String urlString) {
    try {

      @SuppressWarnings("deprecation")
      URL url = new URL(urlString);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();

      conn.setRequestMethod("GET");
      conn.connect();
      return conn;
    } catch (IOException e) {

      e.printStackTrace();

    }

    return null;
  }

  private static int findIndexOfCurrentTime(JSONArray timeList) {

    String currentTime = getCurrentTime();

    for (int i = 0; i < timeList.size(); i++) {
      String time = (String) timeList.get(i);
      if (time.equalsIgnoreCase(currentTime)) {

        return i;
      }
    }
    return 0;
  }

  public static String getCurrentTime() {
    // get current date and time
    LocalDateTime currentDateTime = LocalDateTime.now();

    // DateTimeFormatter formatter =
    // DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
    // throw new UnsupportedOperationException("Unimplemented method
    // 'getCurrentTime'");

    String formattedDateTime = currentDateTime.format(formatter);

    return formattedDateTime;
  }

  private static String convertWeatherCode(long weathercode) {
    String weatherCondition = "";
    if (weathercode == 0L) {
      weatherCondition = "Clear";
    } else if (weathercode <= 3L && weathercode > 0L) {
      weatherCondition = "Cloudy";
    } else if ((weathercode >= 51L && weathercode <= 67)
        || (weathercode >= 80L && weathercode <= 99L)) {

      weatherCondition = "Rain";

    } else if (weathercode >= 71L && weathercode <= 77L) {

      weatherCondition = "Snow";

    }
    return weatherCondition;
  }

}
