package com.ws;

import java.util.LinkedList;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.entities.Almacenamiento;
import com.exception.ServiciosException;


@Path("/almacenamientos")
@Produces("text/plain")
public interface AlmacenamientosRest {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	LinkedList<Almacenamiento> getAll() throws ServiciosException;
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	Almacenamiento get(@PathParam("id") Long id) throws ServiciosException;
	
	@POST
	@Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
	void add(@FormParam("volumen") int volumen, @FormParam("nombre") String nombre, @FormParam("costoop") double costoop, @FormParam("capestiba") double capestiba, @FormParam("cappeso") double cappeso, @FormParam("idEntidadLoc") Long idEntidadLoc)
			throws ServiciosException;
	@PUT
	@Consumes("application/x-www-form-urlencoded")
	@Produces(MediaType.APPLICATION_JSON)
	void update(@FormParam("id") Long id, @FormParam("volumen") int volumen, @FormParam("nombre") String nombre, @FormParam("costoop") double costoop, @FormParam("capestiba") double capestiba, @FormParam("cappeso") double cappeso, @FormParam("idEntidadLoc") Long idEntidadLoc) throws ServiciosException;
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	void delete(@PathParam("id") Long id) throws ServiciosException;

	

}
