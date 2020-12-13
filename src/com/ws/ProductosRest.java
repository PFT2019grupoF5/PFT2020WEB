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

import com.services.ProductoBeanRemote;
import com.entities.Producto;
import com.exception.ServiciosException;

@Stateless
@Path("/productos")
public class ProductosRest {

	
	
	@EJB
	private ProductoBeanRemote productosBeans;
	
	@GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Producto> getAllProductos() throws ServiciosException {
		try{
			List<Producto> listaProductos = productosBeans.getAllProductos(); 
			return listaProductos;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo obtener lista de productos");
		}
    }

	@GET
    @Path("/getById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Producto getProducto(@PathParam("id") Long id) throws ServiciosException {
		try{
			Producto producto = productosBeans.getProducto(id);
			return producto;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo obtener producto con id " + id.toString());
		}
    }
		
	@GET
    @Path("/getNombre/{nombre}")
    @Produces(MediaType.APPLICATION_JSON)
    public Producto getNombre(@PathParam("nombre") String nombre) throws ServiciosException {
		try{
			Producto producto = productosBeans.getNombre(nombre);
			return producto;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo obtener producto con nombre " + nombre);
		}
    }
		
    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Producto addProducto(Producto producto) throws ServiciosException{
    	try{
            productosBeans.add(producto);
            return producto;
        }catch(ServiciosException e){
            e.printStackTrace();
            throw new ServiciosException("No se pudo agregar producto");
        }
    }
	
    @PUT
    @Path("/update/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Producto updateProducto(@PathParam("id") Long id, Producto producto) throws ServiciosException{
        try{
        	producto.setId(id);
            productosBeans.update(producto);
            return producto;
        }catch(ServiciosException e){
            e.printStackTrace();
            throw new ServiciosException("No se pudo modificar producto");
        }
    }
    
    
    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Producto deleteProducto(@PathParam("id") Long id) throws ServiciosException {
    	try{
		    Producto producto = productosBeans.getProducto(id);
			productosBeans.delete(id);
			return producto;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo borrar producto");
		}
    }
    
}
