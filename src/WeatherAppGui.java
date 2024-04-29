import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.json.simple.JSONObject;

public class WeatherAppGui extends JFrame {

  private JSONObject weatherData;

  public WeatherAppGui() {
    // setup gui, add a title
    super("Weather App");

    // configure gui to end the program process once it has been closed
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    setSize(450, 650);
    setLocationRelativeTo(null);
    setLayout(null);
    setResizable(false);

    addGuiComponent();

  }

  public void paint(Graphics draw) { 

    //set background color, To do -- need to check fillOval
    draw.fillOval(40, 40, 60, 50);
    getContentPane().setBackground(new Color(36, 33, 36));

  }

  private void addGuiComponent() {
    // TODO Auto-generated method stub
    JTextField searchTextField = new JTextField();

    searchTextField.setBounds(15, 15, 351, 45);
    // throw new UnsupportedOperationException("Unimplemented method
    // 'addGuiComponent'");
    searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));
    searchTextField.setBackground(new Color(175, 238, 238));

    add(searchTextField);

    JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
    weatherConditionImage.setBounds(0, 110, 450, 245);
    add(weatherConditionImage);

    JLabel temperatureText = new JLabel("10 C");
    temperatureText.setBounds(0, 350, 450, 50);
    temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));
    temperatureText.setForeground(Color.WHITE);

    temperatureText.setHorizontalAlignment(SwingConstants.CENTER);

    add(temperatureText);

    JLabel weatherConditionDesc = new JLabel("Cloudy");
    weatherConditionDesc.setBounds(0, 405, 450, 36);
    weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
    weatherConditionDesc.setForeground(Color.WHITE);
    weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);

    add(weatherConditionDesc);

    JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
    humidityImage.setBounds(15, 500, 85, 55);
    add(humidityImage);

    JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
    humidityText.setBounds(90, 500, 85, 55);
    humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
    humidityText.setForeground(Color.WHITE);
    add(humidityText);

    JLabel windspeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
    windspeedImage.setBounds(220, 500, 74, 66);
    add(windspeedImage);

    JLabel windspeedText = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
    windspeedText.setBounds(310, 500, 85, 55);
    windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
    windspeedText.setForeground(Color.WHITE);
    add(windspeedText);

    JButton searchButton = new JButton(loadImage("src/assets/search.png"));

    searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    searchButton.setBounds(375, 13, 47, 45);
    searchButton.setBackground(new Color(175, 238, 238));
    searchButton.setOpaque(true);
    searchButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        String userInput = searchTextField.getText();
        if (userInput.replaceAll("\\s", "").length() <= 0) {
          return;
        }

        weatherData = WeatherApp.getWeatherData(userInput);

        String weatherCondition = (String) weatherData.get("weather_condition");

        switch (weatherCondition) {
          case "Clear":
            weatherConditionImage.setIcon(loadImage("src/assets/sunny.png"));
            break;
          case "Cloudy":
            weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
            break;
          case "Rain":
            weatherConditionImage.setIcon(loadImage("src/assets/rainy.png"));
            break;
          case "Snow":
            weatherConditionImage.setIcon(loadImage("src/assets/snowy.png"));
            break;
        }

        double temperature = (double) weatherData.get("temperature");
        temperatureText.setText(temperature + " C");

        weatherConditionDesc.setText(weatherCondition);

        long humidity = (long) weatherData.get("humidity");
        humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

        double windspeed = (double) weatherData.get("windspeed");
        windspeedText.setText("<html><b>WindSpeed</b> " + windspeed + "km/h</html>");

      }

    });

    add(searchButton);

  }

  // used to create
  private ImageIcon loadImage(String resourcePath) {
    try {

      BufferedImage image = ImageIO.read(new File(resourcePath));

      return new ImageIcon(image);
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println("Could not load image " + resourcePath);
    return null;
  }
}
