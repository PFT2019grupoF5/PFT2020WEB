package com.ws;

import java.util.LinkedList;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import com.services.CiudadBeanRemote;
import com.entities.Ciudad;
import com.exception.ServiciosException;

@Stateless
public class CiudadesRestBean implements CiudadesRest {

	@EJB
	private CiudadBeanRemote ciudadEJBBean;
	
	@Override
    public void add(String nombre) throws ServiciosException {
        try{
            ciudadEJBBean.add(nombre);
        }catch(Exception e){
            throw new ServiciosException("No se pudo agregar ciudad" + e.getMessage());
        }
    }

	
	@Override
    public void update(Long id, String nombre) throws ServiciosException {
        try{
            ciudadEJBBean.update(id, nombre);
        }catch(Exception e){
            throw new ServiciosException("No se pudo modificar ciudad" + e.getMessage());
        }
    }

	
	@Override
    public void delete(Long id) throws ServiciosException {
		try{
			System.out.println("deleteCiudad-id " + id.toString());
			ciudadEJBBean.delete(id);;
		}catch(Exception e){
			throw new ServiciosException("No se pudo borrar con id " + id.toString() + e.getMessage());
		}
    }
	

	@Override
    public LinkedList<Ciudad> getAll() throws ServiciosException {
		try{
			LinkedList<Ciudad> listaCiudades = ciudadEJBBean.getAll(); 
			return listaCiudades;
		}catch(Exception e){
			throw new ServiciosException("No se pudo obtener lista de ciudades");
		}
    }

	
	@Override
    public Ciudad get(Long id) throws ServiciosException {
		try{
			Ciudad ciudad = ciudadEJBBean.getId(id);
			return ciudad;
		}catch(Exception e){
			throw new ServiciosException("No se pudo obtener ciudad con id " + id.toString() + e.getMessage());
		}
    }

}

