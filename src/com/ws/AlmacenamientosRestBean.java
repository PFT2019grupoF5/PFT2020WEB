package com.ws;

import java.util.LinkedList;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import com.services.AlmacenamientoBeanRemote;
import com.services.EntidadLocBeanRemote;
import com.entities.Almacenamiento;
import com.exception.ServiciosException;

@Stateless
public class AlmacenamientosRestBean implements AlmacenamientosRest {

	@EJB
	private AlmacenamientoBeanRemote almacenamientoEJBBean;
	@EJB
	private EntidadLocBeanRemote entidadLocEJBBean;
	
	@Override
    public void add(int volumen, String nombre, double costoop, double capestiba, double cappeso, Long idEntidadLoc) throws ServiciosException {
        try{
            almacenamientoEJBBean.add(volumen, nombre, costoop, capestiba, cappeso, entidadLocEJBBean.getId(idEntidadLoc));
        }catch(Exception e){
            throw new ServiciosException("No se pudo agregar almacenamiento" + e.getMessage());
        }
    }

	
	@Override
    public void update(Long id, int volumen, String nombre, double costoop, double capestiba, double cappeso, Long idEntidadLoc) throws ServiciosException {
        try{
            almacenamientoEJBBean.update(id, volumen, nombre, costoop, capestiba, cappeso, entidadLocEJBBean.getId(idEntidadLoc));
        }catch(Exception e){
            throw new ServiciosException("No se pudo modificar almacenamiento" + e.getMessage());
        }
    }

	
	@Override
    public void delete(Long id) throws ServiciosException {
		try{
			System.out.println("deleteAlmacenamiento-id " + id.toString());
			almacenamientoEJBBean.delete(id);;
		}catch(Exception e){
			throw new ServiciosException("No se pudo borrar con id " + id.toString() + e.getMessage());
		}
    }
	

	@Override
    public LinkedList<Almacenamiento> getAll() throws ServiciosException {
		try{
			LinkedList<Almacenamiento> listaAlmacenamientos = almacenamientoEJBBean.getAll(); 
			return listaAlmacenamientos;
		}catch(Exception e){
			throw new ServiciosException("No se pudo obtener lista de almacenamientos");
		}
    }

	
	@Override
    public Almacenamiento get(Long id) throws ServiciosException {
		try{
			Almacenamiento almacenamiento = almacenamientoEJBBean.getId(id);
			return almacenamiento;
		}catch(Exception e){
			throw new ServiciosException("No se pudo obtener almacenamiento con id " + id.toString() + e.getMessage());
		}
    }

}

