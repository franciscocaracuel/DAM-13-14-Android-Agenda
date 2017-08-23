package com.izv.agenda;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdaptadorContacto extends ArrayAdapter <Contacto>{
	
	private Context contexto;
	private ArrayList<Contacto> lista;
	private Bitmap bitmap;
	private ImageView ivContacto;

	public AdaptadorContacto(Context c, ArrayList<Contacto> l) {
		super(c, R.layout.item_listview, l);
		this.contexto=c;
		this.lista=l;
	}
	
	@Override
	public View getView(int posicion, View vista, ViewGroup padre){
		
		//Dibuja las lineas
		if(vista==null){ //Este if optimiza el getView
			LayoutInflater i=(LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vista=i.inflate(R.layout.item_listview, null);
		}
		
		//Rellena las lineas
		Contacto contacto=lista.get(posicion);
		
		//Se le asigna el tag a la imagen para poder identificarla cuando se pulse en ella
		ImageView ivMenu=(ImageView)vista.findViewById(R.id.ivMenu);
		ivMenu.setTag(posicion);
		
		TextView tvNombre=(TextView)vista.findViewById(R.id.tvNombre);
		TextView tvTelefono=(TextView)vista.findViewById(R.id.tvTelefono);
		TextView tvEmail=(TextView)vista.findViewById(R.id.tvEmail);
		ivContacto=(ImageView)vista.findViewById(R.id.ivContactoListView);
		
		tvNombre.setText(contacto.getNombre());
		tvTelefono.setText(contacto.getTelefono());
		tvEmail.setText(contacto.getEmail());
		
		//Si en imagen no pone defecto significa que hay una imagen guardada
		String imagen=contacto.getImagen();
		if(!imagen.equals("defecto")){
						
			InputStream stream = null;
            try {
            	
                stream = new FileInputStream(imagen);
                
                //Si se ha borrado la imagen dejara la de por defecto
                if(stream!=null){
                	
                	BitmapFactory.Options options=new BitmapFactory.Options();
    				options.inSampleSize = 8;
    				bitmap = BitmapFactory.decodeStream(stream, null,options);
    				
    				ivContacto.setImageBitmap(bitmap);
                	
                }                
                                
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            
		}
		
		return vista;
		
	}
	
}