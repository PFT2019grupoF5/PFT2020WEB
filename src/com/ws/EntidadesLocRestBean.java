package com.ws;

import java.util.LinkedList;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.services.CiudadBeanRemote;
import com.services.EntidadLocBeanRemote;
import com.entities.EntidadLoc;
import com.enumerated.tipoLoc;
import com.exception.ServiciosException;

@Stateless
public class EntidadesLocRestBean implements EntidadesLocRest {

	@EJB
	private EntidadLocBeanRemote entidadLocEJBBean;
	@EJB
	private CiudadBeanRemote ciudadEJBBean;
	
	@Override
    public void add(int codigo, String nombre, String direccion, tipoLoc tipoLoc, Long idCiudad) throws ServiciosException {
        try{
            entidadLocEJBBean.add(codigo, nombre, direccion, tipoLoc, ciudadEJBBean.getId(idCiudad));
        }catch(Exception e){
            throw new ServiciosException("No se pudo agregar entidadLoc" + e.getMessage());
        }
    }

	
	@Override
    public void update(Long id, int codigo, String nombre, String direccion, tipoLoc tipoLoc, Long idCiudad) throws ServiciosException {
        try{
            entidadLocEJBBean.update(id, codigo, nombre, direccion, tipoLoc, ciudadEJBBean.getId(idCiudad));
        }catch(Exception e){
            throw new ServiciosException("No se pudo modificar entidadLoc" + e.getMessage());
        }
    }

	
	@Override
    public void delete(Long id) throws ServiciosException {
		try{
			entidadLocEJBBean.delete(id);;
		}catch(Exception e){
			throw new ServiciosException("No se pudo borrar con id " + id.toString() + e.getMessage());
		}
    }
	

	@Override
    public LinkedList<EntidadLoc> getAll() throws ServiciosException {
		try{
			LinkedList<EntidadLoc> listaEntidadesLoc = entidadLocEJBBean.getAll(); 
			return listaEntidadesLoc;
		}catch(Exception e){
			throw new ServiciosException("No se pudo obtener lista de entidadesLoc");
		}
    }

	
	@Override
    public EntidadLoc get(Long id) throws ServiciosException {
		try{
			EntidadLoc entidadLoc = entidadLocEJBBean.getId(id);
			return entidadLoc;
		}catch(Exception e){
			throw new ServiciosException("No se pudo obtener entidadLoc con id " + id.toString() + e.getMessage());
		}
    }

}

