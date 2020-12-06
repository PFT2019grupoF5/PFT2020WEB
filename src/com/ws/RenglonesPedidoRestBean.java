package com.ws;

import java.util.LinkedList;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.services.PedidoBeanRemote;
import com.services.ProductoBeanRemote;
import com.services.RenglonPedidoBeanRemote;
import com.entities.RenglonPedido;
import com.exception.ServiciosException;

@Stateless
public class RenglonesPedidoRestBean implements RenglonesPedidoRest {

	@EJB
	private RenglonPedidoBeanRemote renglonPedidoEJBBean;
	@EJB
	private PedidoBeanRemote pedidoEJBBean;
	@EJB
	private ProductoBeanRemote productoEJBBean;
	
	@Override
//    public void add(int rennro, int rencant, Producto producto, Pedido pedido) throws ServiciosException {
    public void add(int rennro, int rencant, Long idProducto, Long idPedido) throws ServiciosException {
        try{
            renglonPedidoEJBBean.add(rennro, rencant, productoEJBBean.getId(idProducto), pedidoEJBBean.getId(idPedido));
        }catch(Exception e){
            throw new ServiciosException("No se pudo agregar renglonPedido" + e.getMessage());
        }
    }

	
	@Override
//    public void update(Long id, int rennro, int rencant, Producto producto, Pedido pedido) throws ServiciosException {
    public void update(Long id, int rennro, int rencant, Long idProducto, Long idPedido) throws ServiciosException {
        try{
//            renglonPedidoEJBBean.update(id, rennro, rencant, producto, pedido);
            renglonPedidoEJBBean.update(id, rennro, rencant,  productoEJBBean.getId(idProducto), pedidoEJBBean.getId(idPedido));
        }catch(Exception e){
            throw new ServiciosException("No se pudo modificar renglonPedido" + e.getMessage());
        }
    }

	
	@Override
    public void delete(Long id) throws ServiciosException {
		try{
			renglonPedidoEJBBean.delete(id);;
		}catch(Exception e){
			throw new ServiciosException("No se pudo borrar con id " + id.toString() + e.getMessage());
		}
    }
	

	@Override
    public LinkedList<RenglonPedido> getAll() throws ServiciosException {
		try{
			LinkedList<RenglonPedido> listaRenglonesPedido = renglonPedidoEJBBean.getAll(); 
			return listaRenglonesPedido;
		}catch(Exception e){
			throw new ServiciosException("No se pudo obtener lista de renglonesPedido");
		}
    }

	
	@Override
    public RenglonPedido get(Long id) throws ServiciosException {
		try{
			RenglonPedido renglonPedido = renglonPedidoEJBBean.getId(id);
			return renglonPedido;
		}catch(Exception e){
			throw new ServiciosException("No se pudo obtener renglonPedido con id " + id.toString() + e.getMessage());
		}
    }

}

