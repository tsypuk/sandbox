package ua.in.smartjava;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import ua.in.smartjava.soap.GetCitiesByCountry;
import ua.in.smartjava.soap.GetCitiesByCountryResponse;
import ua.in.smartjava.soap.GetWeather;
import ua.in.smartjava.soap.GetWeatherResponse;


public class WeatherClient extends WebServiceGatewaySupport {
    private static final Logger log = LoggerFactory.getLogger(WeatherClient.class);

    public GetWeatherResponse getWeather(String country, String city) {

        GetWeather request = new GetWeather();
        request.setCountryName(country);
        request.setCityName(city);

        log.info("Requesting quote for " + city + " " + country);

        GetWeatherResponse response = (GetWeatherResponse) getWebServiceTemplate()
                .marshalSendAndReceive("http://www.webservicex.com/globalweather.asmx",
                        request,
                        new SoapActionCallback("http://www.webserviceX.NET/" + GetWeather.class.getSimpleName()));

        return response;
    }

    public GetCitiesByCountryResponse getCitiesByCountry(String country) {
        GetCitiesByCountry request = new GetCitiesByCountry();
        request.setCountryName("United States");
        return (GetCitiesByCountryResponse) getWebServiceTemplate().marshalSendAndReceive("http://www.webservicex.com/globalweather.asmx",
                request,
                new SoapActionCallback("http://www.webserviceX.NET/" + GetCitiesByCountry.class.getSimpleName()));
    }
}