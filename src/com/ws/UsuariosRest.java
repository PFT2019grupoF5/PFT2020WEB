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

import com.entities.Usuario;
import com.exception.ServiciosException;


@Path("/usuarios")
@Produces("text/plain")
public interface UsuariosRest {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	LinkedList<Usuario> getAll() throws ServiciosException;
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	Usuario get(@PathParam("id") Long id) throws ServiciosException;
	
	@POST
	@Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
	void add(@FormParam("nombre") String nombre, @FormParam("apellido") String apellido, @FormParam("usuario") String nomAcceso, @FormParam("contrasena") String contrasena, @FormParam("correo") String correo, @FormParam("tipoPerfil") String tipoPerfil) throws ServiciosException;
	
	@PUT
	@Consumes("application/x-www-form-urlencoded")
	@Produces(MediaType.APPLICATION_JSON)
	void update(@FormParam("id") Long id, @FormParam("nombre") String nombre, @FormParam("apellido") String apellido, @FormParam("nomAcceso") String nomAcceso, @FormParam("contrasena") String contrasena, @FormParam("correo") String correo, @FormParam("tipoPerfil") String perfil) throws ServiciosException;
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	void delete(@PathParam("id") Long id) throws ServiciosException;
	
	@POST
	@Path("/validarContrasena")
	@Consumes("application/x-www-form-urlencoded")
	@Produces(MediaType.APPLICATION_JSON)
	boolean ValidarContrasena(@FormParam("nomAcceso")String nomAcceso, @FormParam("contrasena") String contrasena) throws ServiciosException;

}
