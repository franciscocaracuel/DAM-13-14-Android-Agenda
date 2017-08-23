package com.izv.agenda;

import java.io.Serializable;

public class Contacto implements Serializable, Comparable <Contacto>{
	

	private static final long serialVersionUID = 7415136961011651873L;
	
	private String nombre, telefono, email, imagen;
	
	public Contacto(){
		
	}
	
	public Contacto(String nombre, String telefono, String email, String imagen){
		this.nombre=nombre;
		this.telefono=telefono;
		this.email=email;
		this.imagen=imagen;
	}
	
	public Contacto(String nombre, String telefono, String email){
		this.nombre=nombre;
		this.telefono=telefono;
		this.email=email;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

	@Override
	public String toString() {
		return "Contacto [nombre=" + nombre + ", telefono=" + telefono
				+ ", email=" + email + ", imagen=" + imagen + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
		result = prime * result + ((telefono == null) ? 0 : telefono.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (!(obj instanceof Contacto))
			return false;
		
		Contacto other = (Contacto) obj;
		
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		
		if (telefono == null) {
			if (other.telefono != null)
				return false;
		} else if (!telefono.equals(other.telefono))
			return false;
		
		return true;
		
	}

	@Override
	public int compareTo(Contacto c) {

		int a=this.nombre.compareToIgnoreCase(c.nombre);
		
		if(a==0){
			return this.telefono.compareToIgnoreCase(c.telefono);
		} else{
			return a;
		}
		
	}

}
