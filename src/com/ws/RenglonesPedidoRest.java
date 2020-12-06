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

import com.entities.Pedido;
import com.entities.Producto;
import com.entities.RenglonPedido;
import com.exception.ServiciosException;


@Path("/renglonesPedido")
@Produces("text/plain")
public interface RenglonesPedidoRest {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	LinkedList<RenglonPedido> getAll() throws ServiciosException;
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	RenglonPedido get(@PathParam("id") Long id) throws ServiciosException;
	
	@POST
	@Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
	void add(@FormParam("rennro") int rennro, @FormParam("rencant") int rencant, @FormParam("producto") Producto producto, @FormParam("pedido") Pedido pedido) throws ServiciosException;
		
	@PUT
	@Consumes("application/x-www-form-urlencoded")
	@Produces(MediaType.APPLICATION_JSON)
	void update(@FormParam("id") Long id, @FormParam("rennro") int rennro, @FormParam("rencant") int rencant, @FormParam("producto") Producto producto, @FormParam("pedido") Pedido pedido) throws ServiciosException;
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	void delete(@PathParam("id") Long id) throws ServiciosException;

}
