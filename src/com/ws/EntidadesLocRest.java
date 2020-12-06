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

import com.entities.Ciudad;
import com.entities.EntidadLoc;
import com.enumerated.tipoLoc;
import com.exception.ServiciosException;


@Path("/entidadesLoc")
@Produces("text/plain")
public interface EntidadesLocRest {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	LinkedList<EntidadLoc> getAll() throws ServiciosException;
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	EntidadLoc get(@PathParam("id") Long id) throws ServiciosException;
	
	@POST
	@Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
	void add(@FormParam("codigo") int codigo, @FormParam("nombre") String nombre, @FormParam("direccion") String direccion, @FormParam("tipoLoc") tipoLoc tipoLoc, @FormParam("ciudad") Ciudad ciudad) throws ServiciosException;
	
	@PUT
	@Consumes("application/x-www-form-urlencoded")
	@Produces(MediaType.APPLICATION_JSON)
	void update(@FormParam("id") Long id, @FormParam("codigo") int codigo, @FormParam("nombre") String nombre, @FormParam("direccion") String direccion, @FormParam("tipoLoc") tipoLoc tipoLoc, @FormParam("ciudad") Ciudad ciudad) throws ServiciosException;
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	void delete(@PathParam("id") Long id) throws ServiciosException;

}
