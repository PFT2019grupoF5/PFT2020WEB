package com.ws;

import java.util.LinkedList;
import javax.ejb.EJB;
import javax.ejb.Stateless;
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
            System.out.println("addUsuario-nombre " + nombre );    
            usuarioEJBBean.add(nombre, apellido, nomAcceso, DigestUtils.md5Hex(contrasena), correo, tipoPerfil.valueOf(perfil.toUpperCase()));
        }catch(Exception e){
            throw new ServiciosException("No se pudo agregar usuario" + e.getMessage());
        }
    }

	
	@Override
    public void update(Long id, String nombre, String apellido, String nomAcceso, String contrasena, String correo, String perfil) throws ServiciosException {
        try{
            System.out.println("updateUsuario-nomAcceso " + nomAcceso);
            usuarioEJBBean.update(id, nombre, apellido, nomAcceso, DigestUtils.md5Hex(contrasena), correo, tipoPerfil.valueOf(perfil.toUpperCase()));
        }catch(Exception e){
            throw new ServiciosException("No se pudo modificar usuario" + e.getMessage());
        }
    }

	
	@Override
    public void delete(Long id) throws ServiciosException {
		try{
			System.out.println("deleteUsuario-id " + id.toString());
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
			System.out.println("getByIdUsuario-id " + id.toString() ); 
			Usuario usuario = usuarioEJBBean.getId(id);
			return usuario;
		}catch(Exception e){
			throw new ServiciosException("No se pudo obtener usuario con id " + id.toString() + e.getMessage());
		}
    }


	@Override
	public boolean ValidarContrasena(String nomAcceso, String contrasena) throws ServiciosException {
		// TODO Auto-generated method stub
		try{
			System.out.println("chequear usr-pws para nomAcceso " + nomAcceso);
			return usuarioEJBBean.ValidarContrasena(nomAcceso, contrasena);
		}catch(Exception e){
			throw new ServiciosException("No se pudo validar usr-pws para nomAcceso " + nomAcceso + e.getMessage());
		}
	}


}

