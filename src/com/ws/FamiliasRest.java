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

import com.entities.Familia;
import com.exception.ServiciosException;


@Path("/familias")
@Produces("text/plain")
public interface FamiliasRest {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	LinkedList<Familia> getAll() throws ServiciosException;
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	Familia get(@PathParam("id") Long id) throws ServiciosException;
	
	@POST
	@Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
	void add(@FormParam("nombre") String nombre, @FormParam("descrip") String descrip, @FormParam("incompat") String incompat) throws ServiciosException;
	
	@PUT
	@Consumes("application/x-www-form-urlencoded")
	@Produces(MediaType.APPLICATION_JSON)
	void update(@FormParam("id") Long id, @FormParam("nombre") String nombre, @FormParam("descrip") String descrip, @FormParam("incompat") String incompat) throws ServiciosException;
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	void delete(@PathParam("id") Long id) throws ServiciosException;

}
