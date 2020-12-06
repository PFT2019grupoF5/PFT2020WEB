package com.ws;

import java.util.LinkedList;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.services.FamiliaBeanRemote;
import com.entities.Familia;
import com.exception.ServiciosException;

@Stateless
public class FamiliasRestBean implements FamiliasRest {

	@EJB
	private FamiliaBeanRemote familiaEJBBean;
	
	@Override
    public void add(String nombre, String descrip, String incompat) throws ServiciosException {
        try{
            familiaEJBBean.add(nombre, descrip, incompat);
        }catch(Exception e){
            throw new ServiciosException("No se pudo agregar familia" + e.getMessage());
        }
    }

	
	@Override
    public void update(Long id, String nombre, String descrip, String incompat) throws ServiciosException {
        try{
            familiaEJBBean.update(id, nombre, descrip, incompat);
        }catch(Exception e){
            throw new ServiciosException("No se pudo modificar familia" + e.getMessage());
        }
    }

	
	@Override
    public void delete(Long id) throws ServiciosException {
		try{
			System.out.println("deleteFamilia-id " + id.toString());
			familiaEJBBean.delete(id);;
		}catch(Exception e){
			throw new ServiciosException("No se pudo borrar con id " + id.toString() + e.getMessage());
		}
    }
	

	@Override
    public LinkedList<Familia> getAll() throws ServiciosException {
		try{
			LinkedList<Familia> listaFamilias = familiaEJBBean.getAll(); 
			return listaFamilias;
		}catch(Exception e){
			throw new ServiciosException("No se pudo obtener lista de familias");
		}
    }

	
	@Override
    public Familia get(Long id) throws ServiciosException {
		try{
			Familia familia = familiaEJBBean.getId(id);
			return familia;
		}catch(Exception e){
			throw new ServiciosException("No se pudo obtener familia con id " + id.toString() + e.getMessage());
		}
    }

}

