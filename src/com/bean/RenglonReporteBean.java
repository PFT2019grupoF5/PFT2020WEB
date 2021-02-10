package com.bean;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import com.entities.Pedido;
import com.entities.RenglonPedido;
import com.entities.RenglonReporte;
import com.exception.ServiciosException;
import com.services.PedidoBeanRemote;
import com.services.RenglonPedidoBeanRemote;

@ManagedBean(name = "renglonReporte")
@RequestScoped
public class RenglonReporteBean {


	private List<Pedido> arrayPedido = new ArrayList<>();
	private List<RenglonPedido> arrayRenglon = new ArrayList<>();
	
	private List<RenglonReporte> arrayReporte = new ArrayList<>();
	
	private Date fechaIni;
	private Date fechaFin;
	
	@EJB
	private PedidoBeanRemote pedidosEJBBean;
	
	@EJB
	private RenglonPedidoBeanRemote renglonesPedidoEJBBean;
	
	public String reporteFechas(){

			if (this.fechaIni.compareTo(this.fechaFin) > 0) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"La Fecha Inicial no puede ser anterior a la Fecha Final", null));
				System.out.println("La Fecha Inicial no puede ser anterior a la Fecha Final");
			}else {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String SfechaIni = sdf.format(fechaIni);
				String SfechaFin = sdf.format(fechaFin);
	
				System.out.println("sfechaIni : " + SfechaIni);
				System.out.println("sfechaIni : " + SfechaFin);
				try {
					arrayPedido = pedidosEJBBean.getPedidosEntreFechas(SfechaIni, SfechaFin);
					for (Pedido pedido : arrayPedido) {
						try {
							arrayRenglon = renglonesPedidoEJBBean.getRenglonesDelPedido(pedido.getId());
							for(RenglonPedido renglonPedido : arrayRenglon) {
								RenglonReporte rr = new RenglonReporte();
								rr.setPedreccodigo(pedido.getPedreccodigo());
								rr.setFecha(pedido.getFecha());
								rr.setPedfecestim(pedido.getPedfecestim());
								rr.setPedestado(pedido.getPedestado());
								rr.setProducto(renglonPedido.getProducto());
								rr.setRencant(renglonPedido.getRencant());
								arrayReporte.add(rr);

								System.out.println("Mostrando reporte entre " + SfechaIni + " y " + SfechaFin);
							}
							
						
						}catch (ServiciosException e) {
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Contacte al administrador. No se pudo mostrar renglones para los pedidos entre estas fechas" + SfechaIni + " y " + SfechaFin, null));
							System.out.println("No se pudieron mostrar los renglones para los pedidos entre estas fechas " + SfechaIni + " y " + SfechaFin);
						}
					}
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Se muestran los pedidos entre las fechas" + SfechaIni + " y " + SfechaFin, null));
					return "reportePedidosFecha";
				} catch (ServiciosException e) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Contacte al administrador. No se pudo generar el reporte", null));
					System.out.println("No se pudo generar el reporte");
			}
		}
		return "reportePedidosFechasPage";
	}

	public Date getFechaIni() {
		return fechaIni;
	}

	public void setFechaIni(Date fechaIni) {
		this.fechaIni = fechaIni;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public List<RenglonReporte> getArrayReporte() {
		return arrayReporte;
	}

	public void setArrayReporte(List<RenglonReporte> arrayReporte) {
		this.arrayReporte = arrayReporte;
	}
	

	
}
