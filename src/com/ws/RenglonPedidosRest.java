package com.ws;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import com.services.RenglonPedidoBeanRemote;
import com.entities.RenglonPedido;
import com.exception.ServiciosException;


@Stateless
@Path("/renglonPedidos")
public class RenglonPedidosRest {
	
	@EJB
	private RenglonPedidoBeanRemote renglonPedidosBeans;
	
	@GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RenglonPedido> getAllRenglonPedidos() throws ServiciosException {
		try{
			List<RenglonPedido> listaRenglonPedidos = renglonPedidosBeans.getAllRenglonesPedido(); 
			return listaRenglonPedidos;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo obtener lista de renglonPedidos");
		}
    }
	
	@GET
    @Path("/getById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public RenglonPedido getRenglonPedido(@PathParam("id") Long id) throws ServiciosException {
		try{
			RenglonPedido renglonPedido = renglonPedidosBeans.getRenglonPedido(id);
			return renglonPedido;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo obtener renglonPedido con id " + id.toString());
		}
    }
		
	@GET
    @Path("/getRenglonesDelPedido/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RenglonPedido> getRenglonesDelPedido(@PathParam("id") Long id) throws ServiciosException {
		try{
			List<RenglonPedido> listaRenglonesDelPedido = renglonPedidosBeans.getRenglonesDelPedido(id); 
			return listaRenglonesDelPedido;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo obtener los renglones del Pedido con id " + id.toString());
		}
    }
		
    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public RenglonPedido addRenglonPedido(RenglonPedido renglonPedido) throws ServiciosException{
        try{
            renglonPedidosBeans.add(renglonPedido);
			return renglonPedido;
        }catch(ServiciosException e){
            e.printStackTrace();
            throw new ServiciosException("No se pudo agregar renglonPedido");
        }
    }
	
    @PUT
    @Path("/update/{id}")
    @Produces(MediaType.APPLICATION_JSON)
      public RenglonPedido updateRenglonPedido(@PathParam("id") Long id, RenglonPedido renglonPedido) throws ServiciosException{
        try{
        	renglonPedido.setId(id);
        	renglonPedidosBeans.update(renglonPedido);
            return renglonPedido;
        }catch(ServiciosException e){
            e.printStackTrace();
            throw new ServiciosException("No se pudo modificar renglonPedido");
        }
    }
    
    
    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public RenglonPedido deleteRenglonPedido(@PathParam("id") Long id) throws ServiciosException {
		try{
			RenglonPedido renglonPedido = renglonPedidosBeans.getRenglonPedido(id);
			renglonPedidosBeans.delete(id);
			return renglonPedido;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo borrar renglonPedido");
		}
    }
    
}