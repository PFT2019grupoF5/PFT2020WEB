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

import com.services.UsuarioBeanRemote;
import com.entities.Usuario;
import com.exception.ServiciosException;
import org.apache.commons.codec.digest.DigestUtils;

@Stateless
@Path("/usuarios")
public class UsuariosRest {

	@EJB
	private UsuarioBeanRemote usuariosBeans;
	
	
	@GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Usuario> getAllUsuarios() throws ServiciosException {
		try{
			List<Usuario> listaUsuarios = usuariosBeans.getAllUsuarios(); 
			return listaUsuarios;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo obtener lista de usuarios");
		}
    }

	@GET
    @Path("/getById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Usuario getUsuario(@PathParam("id") Long id) throws ServiciosException {
		try{
			System.out.println("getByIdUsuario-id " + id.toString() ); 
			Usuario usuario = usuariosBeans.getUsuario(id);
			return usuario;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo obtener usuario con id " + id.toString());
		}
    }
	
    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Usuario addUsuario(Usuario usuario) throws ServiciosException{
        try{
            System.out.println("addUsuario-nombre " + usuario.getNombre());
            //add(String nombre, String apellido, String nomAcceso, String contrasena, String correo, tipoPerfil tipoPerfil)
            usuariosBeans.add(usuario.getNombre(), usuario.getApellido(), usuario.getNomAcceso(), DigestUtils.md5Hex(usuario.getContrasena()), usuario.getCorreo(), usuario.getTipoPerfil());
			return usuario;
        }catch(ServiciosException e){
            e.printStackTrace();
            throw new ServiciosException("No se pudo agregar usuario");
        }
    }
	
    @PUT
    @Path("/update/{id}")
    @Produces(MediaType.APPLICATION_JSON)
      public Usuario updateUsuario(@PathParam("id") Long id, Usuario usuario) throws ServiciosException{
        try{
            System.out.println("updateUsuario-nombre " + usuario.getNombre());
            usuario.setId(id);
            //update(Long id, String nombre, String apellido, String nomAcceso, String contrasena, String correo, tipoPerfil tipoPerfil)
            usuariosBeans.update(id, usuario.getNombre(), usuario.getApellido(), usuario.getNomAcceso(), DigestUtils.md5Hex(usuario.getContrasena()), usuario.getCorreo(), usuario.getTipoPerfil());
            return usuario;
        }catch(ServiciosException e){
            e.printStackTrace();
            throw new ServiciosException("No se pudo modificar usuario");
        }
    }
    
    
    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Usuario deleteUsuario(@PathParam("id") Long id) throws ServiciosException {
		try{
			System.out.println("deleteUsuario-id " + id.toString());
			Usuario usuario = usuariosBeans.getUsuario(id);
			usuariosBeans.delete(id);
			return usuario;
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo borrar usuario");
		}
    }

    
	@GET
    @Path("/getLogin/{nomAcceso}/{contrasena}")
    @Produces(MediaType.APPLICATION_JSON)
    public Usuario getLogin(@PathParam("nomAcceso") String nomAcceso, @PathParam("contrasena") String contrasena) throws ServiciosException {
		try{
			System.out.println("getLogin-nomAcceso-pass " + nomAcceso + " : " + contrasena); 
			Usuario usuario= usuariosBeans.getNA(nomAcceso); 
			
			if (usuario != null ) {   //existe el usuario
				if (DigestUtils.md5Hex(contrasena).equals(usuario.getContrasena())) {
					usuario.setContrasena("+Login-Ok!");
					return usuario; //existe el usuario, y el nomAcceso y contraseña concuerdan
				}else	{
					usuario.setContrasena("+Error!");
					return usuario; //usuario y contraseña no concuerdan, por lo que se devuelve false
				}
		    }else {
		    	return usuario; // NO existe el usuario
		    }			
			
		}catch(ServiciosException e){
			throw new ServiciosException("No se pudo obtener usuario con nomAcceso " + nomAcceso );
		}
    }

}

