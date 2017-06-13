package ua.in.smartjava;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import ua.in.smartjava.soap.GetCitiesByCountryResponse;
import ua.in.smartjava.soap.GetWeatherResponse;

@SpringBootApplication
public class SoapApplication {

	public static void main(String[] args) {
		SpringApplication.run(SoapApplication.class, args);
	}

	@Bean
	CommandLineRunner lookup(WeatherClient weatherClient) {
		return args -> {
			String city = "Selawik";
			String country = "United States";

			if (args.length > 0) {
				city = args[0];
			}
			GetWeatherResponse response = weatherClient.getWeather(country, city);
//			System.err.println(response.getGetWeatherResult());

			GetCitiesByCountryResponse citiesByCountry = weatherClient.getCitiesByCountry(country);
//			System.err.println(citiesByCountry.getGetCitiesByCountryResult());


		};
	}

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("ua.in.smartjava.soap");
		return marshaller;
	}

	@Bean
	public WeatherClient weatherClient(Jaxb2Marshaller marshaller) {
		WeatherClient client = new WeatherClient();
		client.setDefaultUri("http://www.webservicex.com/globalweather.asmx");
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);
		return client;
	}
}