// tag::comment[]
/*******************************************************************************
 * Copyright (c) 2017, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
 // end::comment[]
package io.openliberty.guides.consumingrest.service;

import javax.json.JsonArray;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import io.openliberty.guides.consumingrest.Consumer;
import io.openliberty.guides.consumingrest.model.Artist;
//import io.openliberty.guides.consumingrest.Consumer;

@Path("artists")
public class ArtistResource {

    @Context
    UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonArray getArtists() {
    	return Reader.getArtists();
    }
    
    //Add additional methods here
    
    @GET
    @Path("jsonString")
    @Produces(MediaType.TEXT_PLAIN)
    public String getJsonString() {
    	Jsonb jsonb = JsonbBuilder.create();
    	Artist[] artists = Consumer.consumeWithJsonb(uriInfo.getBaseUri().toString()+"artists");
    	String result = jsonb.toJson(artists);
    	return result;
    }
    
    @GET
    @Path("total/{artist}")
    @Produces(MediaType.TEXT_PLAIN)
    public int getTotalAlbuns( @PathParam("artist") String artist ) {
    	Artist[] artists = Consumer.consumeWithJsonb(uriInfo.getBaseUri().toString()+"artists");
    	for( int index = 0 ; index < artists.length ; index++ ) {
    		if( artists[ index ].name.equals( artist ) ) {
    			return artists[ index ].albums.length;
    		}
    	}
    	return -1;
    }

    @GET
    @Path("total")
    @Produces(MediaType.TEXT_PLAIN)
    public int getTotalArtists() {
    	return Consumer.consumeWithJsonb(uriInfo.getBaseUri().toString()+"artists").length;
    }
    
    
}
