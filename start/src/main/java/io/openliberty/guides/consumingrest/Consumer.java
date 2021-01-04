package io.openliberty.guides.consumingrest;

import java.util.List;
import java.util.stream.Collectors;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import io.openliberty.guides.consumingrest.model.Album;
import io.openliberty.guides.consumingrest.model.Artist;

public class Consumer {

	
	public static Artist[] consumeWithJsonb( String targetURL ) {
		Client client = ClientBuilder.newClient();
		Response response = client.target(targetURL).request().get();
		Artist[] artists = response.readEntity(Artist[].class);
		response.close();
		client.close();
		return artists;
	}
	
	public static Artist[] consumeWithJsonp( String targetURL ) {
		Client client = ClientBuilder.newClient();
		Response response = client.target(targetURL).request().get();
		JsonArray jsonArray = response.readEntity(JsonArray.class);
		response.close();
		client.close();
		return Consumer.collectArtists(jsonArray);
	}
	
	public static Artist[] collectArtists( JsonArray artistsJsonArray ) {
		List<Artist> artists = artistsJsonArray.stream().map( artistJson -> {
			JsonArray jsonArrayAlbuns = ((JsonObject)artistJson).getJsonArray("albuns");
			Artist artist = new Artist(
				((JsonObject)artistJson).getString("name"),
				Consumer.collectAlbuns(jsonArrayAlbuns)
			);
			return artist;
		}).collect(Collectors.toList());
		return artists.toArray(new Artist[artists.size()]);
	}
	
	public static Album[] collectAlbuns( JsonArray albunsJsonArray ) {
		List<Album> albuns = albunsJsonArray.stream().map( albumJsonObject -> {
			Album album = new Album(
				((JsonObject) albumJsonObject).getString("title") ,
				((JsonObject) albumJsonObject).getString("artist") ,
				((JsonObject) albumJsonObject).getInt("ntracks") 
			);
			return album;
		}).collect(Collectors.toList());
		return albuns.toArray(new Album[albuns.size()]);
	}
	
}
