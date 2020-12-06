package com.ws;

import java.util.Date;
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
import com.entities.Producto;
import com.entities.Usuario;
import com.enumerated.Segmentacion;
import com.exception.ServiciosException;


@Path("/productos")
@Produces("text/plain")
public interface ProductosRest {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	LinkedList<Producto> getAll() throws ServiciosException;
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	Producto get(@PathParam("id") Long id) throws ServiciosException;
	
	@POST
	@Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
	void add(@FormParam("nombre") String nombre, @FormParam("lote") String lote, @FormParam("precio") double precio, @FormParam("felab") Date felab, @FormParam("fven") Date fven, @FormParam("peso") double peso, @FormParam("volumen") double volumen, @FormParam("estiba") int estiba, @FormParam("stkMin") double stkMin, @FormParam("stkTotal") double stkTotal, @FormParam("segmentac") Segmentacion segmentac, @FormParam("usuario") Usuario usuario, @FormParam("familia") Familia familia) throws ServiciosException;
	
	@PUT
	@Consumes("application/x-www-form-urlencoded")
	@Produces(MediaType.APPLICATION_JSON)
	void update(@FormParam("id") Long id, @FormParam("nombre") String nombre, @FormParam("lote") String lote, @FormParam("precio") double precio, @FormParam("felab") Date felab, @FormParam("fven") Date fven, @FormParam("peso") double peso, @FormParam("volumen") double volumen, @FormParam("estiba") int estiba, @FormParam("stkMin") double stkMin, @FormParam("stkTotal") double stkTotal, @FormParam("segmentac") Segmentacion segmentac, @FormParam("usuario") Usuario usuario, @FormParam("familia") Familia familia) throws ServiciosException;
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	void delete(@PathParam("id") Long id) throws ServiciosException;

}
