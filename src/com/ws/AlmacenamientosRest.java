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

import com.services.AlmacenamientoBeanRemote;
import com.entities.Almacenamiento;
import com.exception.ServiciosException;

@Stateless
@Path("/almacenamientos")
public class AlmacenamientosRest {

	@EJB
	private AlmacenamientoBeanRemote almacenamientosBeans;
	
	@GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Almacenamiento> getAllAlmacenamientos() throws ServiciosException {
		try{
			List<Almacenamiento> listaAlmacenamientos = almacenamientosBeans.getAllAlmacenamientos(); 
			return listaAlmacenamientos;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo obtener lista de almacenamientos");
		}
    }

	@GET
    @Path("/getById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Almacenamiento getAlmacenamiento(@PathParam("id") Long id) throws ServiciosException {
		try{
			System.out.println("getByIdAlmacenamiento-id " + id.toString() ); 
			Almacenamiento almacenamiento = almacenamientosBeans.getAlmacenamiento(id);
			return almacenamiento;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo obtener almacenamiento con id " + id.toString());
		}
    }
		
    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Almacenamiento addAlmacenamiento(Almacenamiento almacenamiento) throws ServiciosException{
        try{
            System.out.println("addAlmacenamiento-nombre " + almacenamiento.getNombre() );
            //add(int volumen, String nombre, double costoop, double capestiba, double cappeso, EntidadLoc entidadLoc)
			almacenamientosBeans.add(almacenamiento.getVolumen(), almacenamiento.getNombre(), almacenamiento.getCostoop(), almacenamiento.getCapestiba(), almacenamiento.getCappeso(), almacenamiento.getEntidadLoc());
            return almacenamiento;
        }catch(ServiciosException e){
            e.printStackTrace();
            throw new ServiciosException("No se pudo agregar almacenamiento");
        }
    }
	
	
    @PUT
    @Path("/update/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Almacenamiento updateAlmacenamiento(@PathParam("id") Long id, Almacenamiento almacenamiento) throws ServiciosException{
        try{
            System.out.println("updateAlmacenamiento-nombre " + almacenamiento.getNombre() );
            almacenamiento.setId(id);
            //update(Long id, int volumen, String nombre, double costoop, double capestiba, double cappeso, EntidadLoc entidadLoc)
			almacenamientosBeans.update(id, almacenamiento.getVolumen(), almacenamiento.getNombre(), almacenamiento.getCostoop(), almacenamiento.getCapestiba(), almacenamiento.getCappeso(), almacenamiento.getEntidadLoc());
            return almacenamiento;
        }catch(ServiciosException e){
            e.printStackTrace();
            throw new ServiciosException("No se pudo modificar almacenamiento");
        }
    }

    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Almacenamiento deleteAlmacenamiento(@PathParam("id") Long id) throws ServiciosException {
		try{
            System.out.println("deleteAlmacenamiento-id " + id.toString() );
            Almacenamiento almacenamiento = almacenamientosBeans.getAlmacenamiento(id);
			almacenamientosBeans.delete(id);
			return almacenamiento;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo borrar almacenamiento");
		}
    }

}
