package it.io.openliberty.guides.consumingrest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.openliberty.guides.consumingrest.model.Artist;

public class ConsumingRestIT {

	private static String port;
	private static String baseURL;
	private static String targetURL;
	
	private Client client;
	private Response response;
	
	@BeforeAll
	public static void oneTimeSetup() {
		port = System.getProperty("http.port");
		baseURL = "http://localhost:" + port + "/artists/" ;
		targetURL = baseURL + "total/" ;
	}
	
	@BeforeEach
	public void setup() {
		client = ClientBuilder.newClient();
	}
	
	@AfterEach
	public void teardown() {
		client.close();
	}
	
	@Test
	public void testArtistsDeserialization() {
		response = client.target(baseURL+"jsonString").request().get();
		assertResponse(baseURL+"jsonString",response);
		Jsonb jsonb = JsonbBuilder.create();
		
		String expectedString = "{\"name\":\"foo\",\"albums\":"
		        + "[{\"title\":\"album_one\",\"artist\":\"foo\",\"ntracks\":12}]}";
		
		Artist expected = jsonb.fromJson(expectedString, Artist.class);
		
		String actualString = response.readEntity(String.class);
		Artist[] actual = jsonb.fromJson(actualString,Artist[].class);
		
		assertEquals( 
				expected.name , 
				actual[0].name , 
				"Excepted names of artists does not match.");
		response.close();
		
	}
	
	@Test
	public void testJsonBAlbumCount() {
		String[] artists = {"dj","bar","foo"};
		for( int index = 0 ; index < artists.length ; index++ ) {
			response = client.target( targetURL + artists[index]).request().get();
			int excepted = index ;
			int actual = response.readEntity(int.class);
			assertEquals( excepted , actual , 
					"Album count for " + artists[index] + " does not match." );
			response.close();
		}
	}
	
	@Test
	public void testJsonBAlbumCountFormUnknowArtist() {
		response = client.target( targetURL + "unknown-artist" ).request().get();
		int excepted = -1;
		int actual = response.readEntity(int.class);
		assertEquals(excepted, actual , "Unknown artist must have -1 albuns." );
		response.close();
	}
	
	@Test
	public void testJsonPArtistCount() {
		response = client.target( targetURL ).request().get();
		assertResponse(targetURL, response);
		int excepted = 3;
		int actual = response.readEntity(int.class);
		assertEquals(excepted, actual , "Excepted number of artists does not match." );
		response.close();
	}
	
	private void assertResponse( String url , Response response ) {
		assertEquals( 
				200 , 
				response.getStatus() , 
				"Incorrect response code from " + url );
	}
	
}
