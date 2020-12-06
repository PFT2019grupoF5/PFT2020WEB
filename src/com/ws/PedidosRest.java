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

import com.entities.Pedido;
import com.enumerated.estadoPedido;
import com.exception.ServiciosException;


@Path("/pedidos")
@Produces("text/plain")
public interface PedidosRest {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	LinkedList<Pedido> getAll() throws ServiciosException;
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	Pedido get(@PathParam("id") Long id) throws ServiciosException;
	
	@POST
	@Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
	void add(@FormParam("pedfecestim") Date pedfecestim, @FormParam("fecha") Date fecha, @FormParam("pedreccodigo") int pedreccodigo, @FormParam("pedrecfecha") Date pedrecfecha, @FormParam("pedreccomentario") String pedreccomentario, @FormParam("pedestado") estadoPedido pedestado, @FormParam("idUsuario") Long idUsuario) throws ServiciosException;
	
	@PUT
	@Consumes("application/x-www-form-urlencoded")
	@Produces(MediaType.APPLICATION_JSON)
	void update(@FormParam("id") Long id, @FormParam("pedfecestim") Date pedfecestim, @FormParam("fecha") Date fecha, @FormParam("pedreccodigo") int pedreccodigo, @FormParam("pedrecfecha") Date pedrecfecha, @FormParam("pedreccomentario") String pedreccomentario, @FormParam("pedestado") estadoPedido pedestado, @FormParam("idUsuario") Long idUsuario) throws ServiciosException;
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	void delete(@PathParam("id") Long id) throws ServiciosException;

}
