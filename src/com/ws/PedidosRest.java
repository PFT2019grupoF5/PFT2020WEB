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

import com.services.PedidoBeanRemote;
import com.entities.Pedido;
import com.entities.RenglonReporte;
import com.exception.ServiciosException;


@Stateless
@Path("/pedidos")
public class PedidosRest {

	@EJB
	private PedidoBeanRemote pedidosBeans;

	@GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Pedido> getAllPedidos() throws ServiciosException {
		try{
			List<Pedido> listaPedidos = pedidosBeans.getAllPedidos(); 
			return listaPedidos;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo obtener lista de pedidos");
		}
    }
	
	@GET
    @Path("/getById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Pedido getPedido(@PathParam("id") Long id) throws ServiciosException {
		try{ 
			Pedido pedido = pedidosBeans.getPedido(id);
			return pedido;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo obtener pedido con id " + id.toString());
		}
    }
		
    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Pedido addPedido(Pedido pedido) throws ServiciosException{
        try{
            pedidosBeans.add(pedido);
			return pedido;
        }catch(ServiciosException e){
            e.printStackTrace();
            throw new ServiciosException("No se pudo agregar pedido");
        }
    }
	
    @PUT
    @Path("/update/{id}")
    @Produces(MediaType.APPLICATION_JSON)
      public Pedido updatePedido(@PathParam("id") Long id, Pedido pedido) throws ServiciosException{
        try{
            pedido.setId(id);
            pedidosBeans.update(pedido);
            return pedido;
        }catch(ServiciosException e){
            e.printStackTrace();
            throw new ServiciosException("No se pudo modificar pedido");
        }
    }
    
    
    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Pedido deletePedido(@PathParam("id") Long id) throws ServiciosException {
		try{
			System.out.println("deletePedido-id " + id.toString());
			Pedido pedido = pedidosBeans.getPedido(id);
			pedidosBeans.delete(id);
			return pedido;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo borrar pedido");
		}
    }
    
	@GET
    @Path("/getPedidosEntreFechas/{fechaDesde}/{fechaHasta}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Pedido> getPedidosEntreFechas(@PathParam("fechaDesde") String fechaDesde, @PathParam("fechaHasta") String fechaHasta) throws ServiciosException {
		try{
			List<Pedido> listaPedidos = pedidosBeans.getPedidosEntreFechas(fechaDesde, fechaHasta); 
			return listaPedidos;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo obtener el reporte de pedidos");
		}
    }
    
	@GET
    @Path("/getReporteEntreFechas/{fechaDesde}/{fechaHasta}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RenglonReporte> getReporteEntreFechas(@PathParam("fechaDesde") String fechaDesde, @PathParam("fechaHasta") String fechaHasta) throws ServiciosException {
		try{
			List<RenglonReporte> listaREnglonesReporte = pedidosBeans.getReporteEntreFechas(fechaDesde, fechaHasta); 
			return listaREnglonesReporte;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo obtener el reporte de pedidos");
		}
    }
    
    
}
