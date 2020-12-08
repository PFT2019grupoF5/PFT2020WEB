package com.ws;

import java.util.LinkedList;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.PersistenceException;

import org.apache.commons.codec.digest.DigestUtils;
import com.services.UsuarioBeanRemote;
import com.entities.Usuario;
import com.enumerated.tipoPerfil;
import com.exception.ServiciosException;

@Stateless
public class UsuariosRestBean implements UsuariosRest {

	@EJB
	private UsuarioBeanRemote usuarioEJBBean;
	
	@Override
    public void add(String nombre, String apellido, String nomAcceso, String contrasena, String correo, String perfil) throws ServiciosException {
        try{
            usuarioEJBBean.add(nombre, apellido, nomAcceso, DigestUtils.md5Hex(contrasena), correo, tipoPerfil.valueOf(perfil.toUpperCase()));
        }catch(Exception e){
            throw new ServiciosException("No se pudo agregar usuario" + e.getMessage());
        }
    }

	
	@Override
    public void update(Long id, String nombre, String apellido, String nomAcceso, String contrasena, String correo, String perfil) throws ServiciosException {
        try{
            usuarioEJBBean.update(id, nombre, apellido, nomAcceso, DigestUtils.md5Hex(contrasena), correo, tipoPerfil.valueOf(perfil.toUpperCase()));
        }catch(Exception e){
            throw new ServiciosException("No se pudo modificar usuario" + e.getMessage());
        }
    }

	
	@Override
    public void delete(Long id) throws ServiciosException {
		try{
			usuarioEJBBean.delete(id);;
		}catch(Exception e){
			throw new ServiciosException("No se pudo borrar con id " + id.toString() + e.getMessage());
		}
    }
	

	@Override
    public LinkedList<Usuario> getAll() throws ServiciosException {
		try{
			LinkedList<Usuario> listaUsuarios = usuarioEJBBean.getAll(); 
			return listaUsuarios;
		}catch(Exception e){
			throw new ServiciosException("No se pudo obtener lista de usuarios");
		}
    }

	
	@Override
    public Usuario get(Long id) throws ServiciosException {
		try{
			Usuario usuario = usuarioEJBBean.getId(id);
			return usuario;
		}catch(Exception e){
			throw new ServiciosException("No se pudo obtener usuario con id " + id.toString() + e.getMessage());
		}
    }


	@Override
    public Usuario getNA(String nomAcceso) throws ServiciosException {
		try{
			Usuario usuario = usuarioEJBBean.getNA(nomAcceso);
			return usuario;
		}catch(Exception e){
			throw new ServiciosException("No se pudo obtener usuario con nomAcceso " + nomAcceso + e.getMessage());
		}
    }

/*
	@Override
	public Usuario ValidarContrasena(String nomAcceso, String contrasena) throws ServiciosException {
		// TODO Auto-generated method stub
		Usuario usuario = null;
		System.out.println("nomAcceso " + nomAcceso + "contrasena" + contrasena);
		try{
			System.out.println("chequear usr-pws para nomAcceso " + nomAcceso);
			if (usuarioEJBBean.ValidarContrasena(nomAcceso, contrasena)) {
				usuario = usuarioEJBBean.getNA(nomAcceso);
				return usuario; 
			};
		}catch(Exception e){
			throw new ServiciosException("No se pudo validar usr-pws para nomAcceso " + nomAcceso + e.getMessage());
		}
		return usuario;
	}

*/	
	
	@Override
    public Usuario ValidarContrasena(String nomAcceso, String contrasena) throws ServiciosException {
		try{
			System.out.println("getLogin-nomAcceso-contrasena " + nomAcceso + " : " + contrasena); 
			Usuario usuario= usuarioEJBBean.getNA(nomAcceso); 
			
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
			
		}catch(PersistenceException e){
			throw new ServiciosException("No se pudo obtener usuario con nomAcceso " + nomAcceso );
		}
    }
	
	

}

