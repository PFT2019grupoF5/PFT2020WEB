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
			System.out.println("getByIdProducto-id " + id.toString() ); 
			Producto producto = productosBeans.getProducto(id);
			return producto;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo obtener producto con id " + id.toString());
		}
    }
		
    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Producto addProducto(Producto producto) throws ServiciosException{
    	try{
            System.out.println("addProducto-nombre " + producto.getNombre());    
            System.out.println("addProducto-Felab  " + producto.getFelab().toString());    
            System.out.println("addProducto-Fven   " + producto.getFven().toString());
            //add(String nombre, String lote, double precio, Date felab, Date fven, double peso, double volumen, int estiba, double stkMin, double stkTotal, Segmentacion segmentac, Usuario usuario, Familia familia)
            productosBeans.add(producto.getNombre(), producto.getLote(), producto.getPrecio(), producto.getFelab(), producto.getFven(), producto.getPeso(), producto.getVolumen(), producto.getEstiba(), producto.getStkMin(), producto.getStkTotal(), producto.getSegmentac(), producto.getUsuario(), producto.getFamilia());
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
            System.out.println("updateProducto-nombre " + producto.getNombre());
            producto.setId(id);
            // update(Long id, String nombre, String lote, double precio, Date felab, Date fven, double peso, double volumen, int estiba, double stkMin, double stkTotal, Segmentacion segmentac, Usuario usuario, Familia familia)
            productosBeans.update(id, producto.getNombre(), producto.getLote(), producto.getPrecio(), producto.getFelab(), producto.getFven(), producto.getPeso(), producto.getVolumen(), producto.getEstiba(), producto.getStkMin(), producto.getStkTotal(), producto.getSegmentac(), producto.getUsuario(), producto.getFamilia());
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
			System.out.println("deleteProducto-id " + id.toString());
		    Producto producto = productosBeans.getProducto(id);
			productosBeans.delete(id);
			return producto;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo borrar producto");
		}
    }
    
}
