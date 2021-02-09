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

import com.services.EntidadLocBeanRemote;
import com.entities.EntidadLoc;
import com.exception.ServiciosException;


@Stateless
@Path("/entidadesLoc")
public class EntidadLocRest {

	@EJB
	private EntidadLocBeanRemote entidadesLocBeans;

	@GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public List<EntidadLoc> getAllEntidadesLoc() throws ServiciosException {
		try{
			List<EntidadLoc> listaEntidadesLoc = entidadesLocBeans.getAllEntidadesLoc(); 
			return listaEntidadesLoc;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo obtener lista de entidadesLoc");
		}
    }

	@GET
    @Path("/getById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public EntidadLoc getEntidadLoc(@PathParam("id") Long id) throws ServiciosException {
		try{
			EntidadLoc entidadLoc = entidadesLocBeans.getEntidadLoc(id);
			return entidadLoc;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo obtener entidadLoc con id " + id.toString());
		}
    }
		
    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public EntidadLoc addEntidadLoc(EntidadLoc entidadLoc) throws ServiciosException{
        try{
            entidadesLocBeans.add(entidadLoc);
			return entidadLoc;
        }catch(ServiciosException e){
            e.printStackTrace();
            throw new ServiciosException("No se pudo agregar entidadLoc");
        }
    }
	
    @PUT
    @Path("/update/{id}")
    @Produces(MediaType.APPLICATION_JSON)
      public EntidadLoc updateEntidadLoc(@PathParam("id") Long id, EntidadLoc entidadLoc) throws ServiciosException{
        try{
        	entidadLoc.setId(id);
            entidadesLocBeans.update(entidadLoc);
            return entidadLoc;
        }catch(ServiciosException e){
            e.printStackTrace();
            throw new ServiciosException("No se pudo modificar entidadLoc");
        }
    }
    
    
    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public EntidadLoc deleteEntidadLoc(@PathParam("id") Long id) throws ServiciosException {
		try{
			EntidadLoc entidadLoc = entidadesLocBeans.getEntidadLoc(id);
			entidadesLocBeans.delete(id);
			return entidadLoc;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo borrar entidadLoc");
		}
    }
    
}
