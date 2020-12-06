package com.ws;

import java.util.Date;
import java.util.LinkedList;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.services.ProductoBeanRemote;
import com.entities.Familia;
import com.entities.Producto;
import com.entities.Usuario;
import com.enumerated.Segmentacion;
import com.exception.ServiciosException;

@Stateless
public class ProductosRestBean implements ProductosRest {

	@EJB
	private ProductoBeanRemote productoEJBBean;
	
	@Override
    public void add(String nombre, String lote, double precio, Date felab, Date fven, double peso, double volumen, int estiba, double stkMin, double stkTotal, Segmentacion segmentac, Usuario usuario, Familia familia) throws ServiciosException {
        try{
            productoEJBBean.add(nombre, lote, precio, felab, fven, peso, volumen, estiba, stkMin, stkTotal, segmentac, usuario, familia);
        }catch(Exception e){
            throw new ServiciosException("No se pudo agregar producto" + e.getMessage());
        }
    }

	
	@Override
    public void update(Long id, String nombre, String lote, double precio, Date felab, Date fven, double peso, double volumen, int estiba, double stkMin, double stkTotal, Segmentacion segmentac, Usuario usuario, Familia familia) throws ServiciosException {
        try{
            productoEJBBean.update(id, nombre, lote, precio, felab, fven, peso, volumen, estiba, stkMin, stkTotal, segmentac, usuario, familia);
        }catch(Exception e){
            throw new ServiciosException("No se pudo modificar producto" + e.getMessage());
        }
    }

	
	@Override
    public void delete(Long id) throws ServiciosException {
		try{
			productoEJBBean.delete(id);;
		}catch(Exception e){
			throw new ServiciosException("No se pudo borrar con id " + id.toString() + e.getMessage());
		}
    }
	

	@Override
    public LinkedList<Producto> getAll() throws ServiciosException {
		try{
			LinkedList<Producto> listaProductos = productoEJBBean.getAll(); 
			return listaProductos;
		}catch(Exception e){
			throw new ServiciosException("No se pudo obtener lista de productos");
		}
    }

	
	@Override
    public Producto get(Long id) throws ServiciosException {
		try{
			Producto producto = productoEJBBean.getId(id);
			return producto;
		}catch(Exception e){
			throw new ServiciosException("No se pudo obtener producto con id " + id.toString() + e.getMessage());
		}
    }

}

