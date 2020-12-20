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

import com.services.CiudadBeanRemote;
import com.entities.Ciudad;
import com.exception.ServiciosException;


@Stateless
@Path("/ciudades")
public class CiudadesRest {

	@EJB
	private CiudadBeanRemote ciudadesBeans;

	@GET
	@Path("/getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Ciudad> getAllCiudades() throws ServiciosException {
		try {
			List<Ciudad> listaCiudades = ciudadesBeans.getAllCiudades();
			return listaCiudades;
		} catch (ServiciosException e) {
			throw new ServiciosException("No se pudo obtener lista de ciudades");
		}
	}

	@GET
	@Path("/getById/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Ciudad getCiudad(@PathParam("id") Long id) throws ServiciosException {
		try {
			Ciudad ciudad = ciudadesBeans.getCiudad(id);
			return ciudad;
		} catch (ServiciosException e) {
			throw new ServiciosException("No se pudo obtener ciudad con id " + id.toString());
		}
	}

	@POST
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	public Ciudad addCiudad(Ciudad ciudad) throws ServiciosException {
		try {
			ciudadesBeans.add(ciudad);
			return ciudad;
		} catch (ServiciosException e) {
			e.printStackTrace();
			throw new ServiciosException("No se pudo agregar ciudad");
		}
	}

	@PUT
	@Path("/update/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Ciudad updateCiudad(@PathParam("id") Long id, Ciudad ciudad) throws ServiciosException {
		try {
			ciudad.setId(id);
			ciudadesBeans.update(ciudad);
			return ciudad;
		} catch (ServiciosException e) {
			e.printStackTrace();
			throw new ServiciosException("No se pudo modificar ciudad");
		}
	}

	@DELETE
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Ciudad deleteCiudad(@PathParam("id") Long id) throws ServiciosException {
		try {
			Ciudad ciudad = ciudadesBeans.getCiudad(id);
			ciudadesBeans.delete(id);
			return ciudad;
		} catch (ServiciosException e) {
			throw new ServiciosException("No se pudo borrar ciudad");
		}
	}

}
