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

import com.entities.Movimiento;
import com.enumerated.tipoMovimiento;
import com.exception.ServiciosException;


@Path("/movimientos")
@Produces("text/plain")
public interface MovimientosRest {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	LinkedList<Movimiento> getAll() throws ServiciosException;
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	Movimiento get(@PathParam("id") Long id) throws ServiciosException;
	
	@POST
	@Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
	void add(@FormParam("fecha") Date fecha, @FormParam("cantidad") int cantidad, @FormParam("descripcion") String descripcion, @FormParam("costo") double costo, @FormParam("tipoMov") tipoMovimiento tipoMov, @FormParam("idProducto") Long idProducto, @FormParam("idAlmacenamiento") Long idAlmacenamiento) throws ServiciosException;
	
	@PUT
	@Consumes("application/x-www-form-urlencoded")
	@Produces(MediaType.APPLICATION_JSON)
	void update(@FormParam("id") Long id, @FormParam("fecha") Date fecha, @FormParam("cantidad") int cantidad, @FormParam("descripcion") String descripcion, @FormParam("costo") double costo, @FormParam("tipoMov") tipoMovimiento tipoMov, @FormParam("idProducto") Long idProducto, @FormParam("idAlmacenamiento") Long idAlmacenamiento) throws ServiciosException;
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	void delete(@PathParam("id") Long id) throws ServiciosException;

}
