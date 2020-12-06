package com.ws;

import java.util.Date;
import java.util.LinkedList;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.services.AlmacenamientoBeanRemote;
import com.services.MovimientoBeanRemote;
import com.services.ProductoBeanRemote;
import com.entities.Movimiento;
import com.enumerated.tipoMovimiento;
import com.exception.ServiciosException;

@Stateless
public class MovimientosRestBean implements MovimientosRest {

	@EJB
	private MovimientoBeanRemote movimientoEJBBean;
	@EJB
	private ProductoBeanRemote productoEJBBean;
	@EJB
	private AlmacenamientoBeanRemote almacenamientoEJBBean;
	
	@Override
    public void add(Date fecha, int cantidad, String descripcion, double costo, tipoMovimiento tipoMov, Long idProducto, Long idAlmacenamiento) throws ServiciosException {
        try{
            movimientoEJBBean.add(fecha, cantidad, descripcion, costo, tipoMov, productoEJBBean.getId(idProducto), almacenamientoEJBBean.getId(idAlmacenamiento));
        }catch(Exception e){
            throw new ServiciosException("No se pudo agregar movimiento" + e.getMessage());
        }
    }

	
	@Override
    public void update(Long id, Date fecha, int cantidad, String descripcion, double costo, tipoMovimiento tipoMov, Long idProducto, Long idAlmacenamiento) throws ServiciosException {
        try{
            movimientoEJBBean.update(id, fecha, cantidad, descripcion, costo, tipoMov, productoEJBBean.getId(idProducto), almacenamientoEJBBean.getId(idAlmacenamiento));
        }catch(Exception e){
            throw new ServiciosException("No se pudo modificar movimiento" + e.getMessage());
        }
    }

	
	@Override
    public void delete(Long id) throws ServiciosException {
		try{
			movimientoEJBBean.delete(id);;
		}catch(Exception e){
			throw new ServiciosException("No se pudo borrar con id " + id.toString() + e.getMessage());
		}
    }
	

	@Override
    public LinkedList<Movimiento> getAll() throws ServiciosException {
		try{
			LinkedList<Movimiento> listaMovimientos = movimientoEJBBean.getAll(); 
			return listaMovimientos;
		}catch(Exception e){
			throw new ServiciosException("No se pudo obtener lista de movimientos");
		}
    }

	
	@Override
    public Movimiento get(Long id) throws ServiciosException {
		try{
			Movimiento movimiento = movimientoEJBBean.getId(id);
			return movimiento;
		}catch(Exception e){
			throw new ServiciosException("No se pudo obtener movimiento con id " + id.toString() + e.getMessage());
		}
    }

}

