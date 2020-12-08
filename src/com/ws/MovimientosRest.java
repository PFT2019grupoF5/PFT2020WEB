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

import com.services.MovimientoBeanRemote;
import com.entities.Movimiento;
import com.exception.ServiciosException;

@Stateless
@Path("/movimientos")
public class MovimientosRest {

	@EJB
	private MovimientoBeanRemote movimientosBeans;

	@GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Movimiento> getAllMovimientos() throws ServiciosException {
		try{
			List<Movimiento> listaMovimientos = movimientosBeans.getAllMovimientos(); 
			return listaMovimientos;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo obtener lista de movimientos");
		}
    }
	
	@GET
    @Path("/getById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Movimiento getMovimiento(@PathParam("id") Long id) throws ServiciosException {
		try{
			System.out.println("getByIdMovimiento-id " + id.toString() ); 
			Movimiento movimiento = movimientosBeans.getMovimiento(id);
			return movimiento;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo obtener movimiento con id " + id.toString());
		}
    }
		
    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Movimiento addMovimiento(Movimiento movimiento) throws ServiciosException{
        try{
            System.out.println("addMovimiento-id " + movimiento.getDescripcion() );    
            System.out.println("addMovimiento-Fecha  " + movimiento.getFecha().toString());
            //add(Date fecha, int cantidad, String descripcion, double costo, tipoMovimiento tipoMov, Producto producto, Almacenamiento almacenamiento)
            movimientosBeans.add(movimiento.getFecha(), movimiento.getCantidad(), movimiento.getDescripcion(), movimiento.getCosto(), movimiento.getTipoMov(), movimiento.getProducto(), movimiento.getAlmacenamiento());
			return movimiento;
        }catch(ServiciosException e){
            e.printStackTrace();
            throw new ServiciosException("No se pudo agregar movimiento");
        }
    }
	
    @PUT
    @Path("/update/{id}")
    @Produces(MediaType.APPLICATION_JSON)
      public Movimiento updateMovimiento(@PathParam("id") Long id, Movimiento movimiento) throws ServiciosException{
        try{
            System.out.println("updateMovimiento-id " + movimiento.getId().toString() );
            movimiento.setId(id);
            //update(Long id, Date fecha, int cantidad, String descripcion, double costo, tipoMovimiento tipoMov, Producto producto,  Almacenamiento almacenamiento)
            movimientosBeans.update(id, movimiento.getFecha(), movimiento.getCantidad(), movimiento.getDescripcion(), movimiento.getCosto(), movimiento.getTipoMov(), movimiento.getProducto(), movimiento.getAlmacenamiento());
            return movimiento;
        }catch(ServiciosException e){
            e.printStackTrace();
            throw new ServiciosException("No se pudo modificar movimiento");
        }
    }
    
    
    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Movimiento deleteMovimiento(@PathParam("id") Long id) throws ServiciosException {
		try{
			System.out.println("deleteMovimiento-id " + id.toString());
			Movimiento movimiento = movimientosBeans.getMovimiento(id);
			movimientosBeans.delete(id);
			return movimiento;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo borrar movimiento");
		}
    }

}
