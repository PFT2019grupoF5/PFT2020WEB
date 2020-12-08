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

import com.services.FamiliaBeanRemote;
import com.entities.Familia;
import com.exception.ServiciosException;

@Stateless
@Path("/familias")
public class FamiliasRest {

	@EJB
	private FamiliaBeanRemote familiasBeans;

	@GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Familia> getAllFamilias() throws ServiciosException {
		try{
			List<Familia> listaFamilias = familiasBeans.getAllFamilias(); 
			return listaFamilias;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo obtener lista de familias");
		}
    }

	@GET
    @Path("/getById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Familia getFamilia(@PathParam("id") Long id) throws ServiciosException {
		try{
			System.out.println("getByIdFamilia-id " + id.toString() ); 
			Familia familia = familiasBeans.getFamilia(id);
			return familia;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo obtener familia con id " + id.toString());
		}
    }
	
    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Familia addFamilia(Familia familia) throws ServiciosException{
        try{
            System.out.println("addFamilia-nombre " + familia.getNombre() );
            //add(String nombre, String descrip, String incompat) 
            familiasBeans.add(familia.getNombre(), familia.getDescrip(), familia.getIncompat());
			return familia;
        }catch(ServiciosException e){
            e.printStackTrace();
            throw new ServiciosException("No se pudo agregar familia");
        }
    }
	
    @PUT
    @Path("/update/{id}")
    @Produces(MediaType.APPLICATION_JSON)
      public Familia updateFamilia(@PathParam("id") Long id, Familia familia) throws ServiciosException{
        try{
            System.out.println("updateFamilia-nombre " + familia.getNombre());
            familia.setId(id);
            //update(Long id, String nombre, String descrip, String incompat)
            familiasBeans.update(id, familia.getNombre(), familia.getDescrip(), familia.getIncompat());
            return familia;
        }catch(ServiciosException e){
            e.printStackTrace();
            throw new ServiciosException("No se pudo modificar familia");
        }
    }
    
    
    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Familia deleteFamilia(@PathParam("id") Long id) throws ServiciosException {
		try{
			System.out.println("deleteFamilia-id " + id.toString());
			Familia familia = familiasBeans.getFamilia(id);
			familiasBeans.delete(id);
			return familia;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo borrar familia");
		}
    }
    
}
