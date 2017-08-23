package com.izv.agenda;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;


import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends Activity {
	
	private File archivo;
	private static ArrayList<Contacto> listaContactos;
	private int posicion;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		//Se inicializa el listado de contactos
    	listaContactos=new ArrayList<Contacto>();
    	
    	cargarArchivo();
			
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public static ArrayList<Contacto> getListaContactos(){
    	
    	return listaContactos;
    	
    }
    
    public static void setListaContactos(ArrayList<Contacto> lc){
    	
    	listaContactos=lc;
    	
    }
    
    public void cargarArchivo(){
    	
    	//Se dice donde estara el archivo con los contactos
    	archivo=new File(getFilesDir(), "agenda.xml");
    	
    	//Si existe se cargan los contactos, si no no se hace nada
    	if(archivo.exists()){
    		
    		cargarContactos();
    		    		
    	}
    	
    }
    
    public void cargarContactos(){
    	
    	XmlPullParser lectorXml=Xml.newPullParser();
    	
    	Contacto c;
    	
    	//Como el email se puede dejar en blanco el if no funciona bien del todo. Al poner emailVisto en el if sabemos si
    	//ha pasado por el nodo email
    	boolean emailVisto=false;
    	
    	//Se inicializan las etiquetas para la comprobacion de cada while
    	String etiqueta, nombre="", telefono="", email="", imagen="";
    	
    	try {
    		
    		//Se asigna a lectorXml el archivo que va a tener el xml
			lectorXml.setInput(new FileInputStream(archivo), "utf-8");
			
			//Con esto sabemos por donde va recorriendo el archivo xml
			int evento=lectorXml.getEventType();			

			//Cuando el lector llegue al final se termina el while
	    	while(evento!=XmlPullParser.END_DOCUMENT){
	    		
	    		//Cada vez que comience una etiqueta entraremos en el if
	    		if(evento==XmlPullParser.START_TAG){
	    			
	    			//Se guarda el nombre de la etiqueta para saber si es la que nos interesa. Cuando sea
	    			//la que nos interese guardaremos su contenido en las variables delaradas antes
	    			etiqueta=lectorXml.getName();
	    			
	    			if(etiqueta.compareTo("nombre")==0){
	    				
	    				nombre=lectorXml.nextText();
	    				
	    			} else if(etiqueta.compareTo("telefono")==0){
	    				
	    				telefono=lectorXml.nextText();
	    				
	    			} else if(etiqueta.compareTo("email")==0){
	    				
	    				email=lectorXml.nextText();
	    				
	    				//Para que entre en el if lo ponemos a true
	    				emailVisto=true;
	    				
	    			} else if(etiqueta.compareTo("imagen")==0){
	    				
	    				imagen=lectorXml.nextText();
	    				
	    			}
	    			
	    		}
	    		
	    		//Para que no se repita la misma linea se adelanta una linea del lector
	    		evento=lectorXml.next();
	    		
	    		//Como empezaban las variables en blanco, cuando tengan todas un valor podremos hacer la insercion en el array
	    		if(nombre.compareTo("")!=0 && telefono.compareTo("")!=0 && emailVisto && imagen.compareTo("")!=0){
	    			
	    			c=new Contacto(nombre, telefono, email, imagen);

	    			listaContactos.add(c);
	    			
	    			//Para que este if siga funcionando hay que igualar a nada las variables y poner a false el emailVisto
	    			nombre="";
	    			telefono="";
	    			email="";
	    			emailVisto=false;
	    			imagen="";
	    			
	    		}
	    			    		
	    	}
	    		    	
	    	cargarListView();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    }
    
    public void anadirContacto(View v){
    	
    	//Se crea la intencion de crear un contacto y se manda a la activity que conseguira los datos
    	Intent intencion=new Intent(getApplicationContext(), AnadirContacto.class);
    	
    	//Se pone 0 para diferenciarlo de editar contacto
    	startActivityForResult(intencion, 0);
    	
    }
    
    //Este metodo es el atillo que nos trae los datos del contacto creado si el resultado es OK
    @Override
    public void onActivityResult(int ctePeticion, int cteResultado, Intent datos){

    	//Si venimos de añadir
		if(ctePeticion==0){
				

		    //Si se ha pulsado el boton añadir o editar
			if(cteResultado==Activity.RESULT_OK){
		    	
    			    		
        		//Los datos vendran en un array de string
        		ArrayList<String> listaAux=datos.getStringArrayListExtra("listaContactos");

        		//Se crea un objeto contacto que tendra todos los datos del contacto. Ya sabemos que no se repite
        		Contacto cAux=new Contacto(listaAux.get(0), listaAux.get(1), listaAux.get(2), listaAux.get(3));

        		//Metemos en el array el nuevo contacto por si mas tarde hace falta
        		listaContactos.add(cAux);

        		/*//Metemos en el adaptador del listview el nuevo objeto
        		adaptador.add(cAux);

        		//Hay que decirle que hemos metido un nuevo objeto
        		adaptador.notifyDataSetChanged();*/
        		    
        		//Como no me funciona cargo el listView entero
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////	
        		cargarListView();
    	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
        		    			
        		//Se guarda el xml con el nuevo contacto
        		guardarArchivo();
        		
        		//Se avisa que el contacto se ha añadido
        		Toast.makeText(this, R.string.info_contacto_añadido, Toast.LENGTH_SHORT).show();
        		
			}else{    		
	    		Toast.makeText(this, R.string.info_contacto_no_añadido, Toast.LENGTH_SHORT).show();
	    	}
        	
    	//Si venimos de editar
		} else if(ctePeticion==1){
			
			//Sabemos que si es  100 es que se ha borrado el contacto
			if(cteResultado==100){
	    		
	    		cargarListView();
				guardarArchivo();
				Toast.makeText(this, R.string.info_contacto_eliminado, Toast.LENGTH_SHORT).show();
				
			//Si se ha editado
			} else if(cteResultado==Activity.RESULT_OK){
				
				cargarListView();
				guardarArchivo();
				Toast.makeText(this, R.string.info_contacto_editado, Toast.LENGTH_SHORT).show();
				
			} else{
				
				Toast.makeText(this, R.string.info_contacto_no_editado, Toast.LENGTH_SHORT).show();
				
			}
				
    	} 
    				
    }
    
    private void guardarArchivo(){
    			
		try {
			
			FileOutputStream fosXml = new FileOutputStream(archivo);
			
			XmlSerializer archivoXml= Xml.newSerializer();
			archivoXml.setOutput(fosXml, "UTF-8");
			archivoXml.startDocument(null, Boolean.valueOf(true));
			archivoXml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

			archivoXml.startTag(null, "agenda");
			
			//Una vez iniciado el archivo xml recorre el array y se van poniendo todos sus atributos
			for(int i=0; i<listaContactos.size();i++){
				
				//Se crea cAux en el que se iran volcando los contactos
				Contacto cAux=listaContactos.get(i);
				
				//contacto es el nodo principal de cada contacto
				archivoXml.startTag(null, "contacto");
				//aqui se iran poniendo cada valor de cada contacto
				archivoXml.startTag(null, "nombre");
				archivoXml.text(cAux.getNombre());
				archivoXml.endTag(null, "nombre");
				
				archivoXml.startTag(null, "telefono");
				archivoXml.text(cAux.getTelefono());
				archivoXml.endTag(null, "telefono");
				
				archivoXml.startTag(null, "email");
				archivoXml.text(cAux.getEmail());
				archivoXml.endTag(null, "email");
				
				archivoXml.startTag(null, "imagen");
				archivoXml.text(cAux.getImagen());
				archivoXml.endTag(null, "imagen");
				
				archivoXml.endTag(null, "contacto");
				
			}
			
			archivoXml.endDocument();
			archivoXml.flush();
			fosXml.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void menuImagen(View v){
    	
    	//Metemos en posicion el tag que le hemos puesto a cada imagen en la clase adaptador
    	//que coincidia con la posicion en el listview
    	posicion=Integer.parseInt(v.getTag().toString());
    	
    	//Se crea un popupMenu
    	PopupMenu popup = new PopupMenu(getApplicationContext(), v);
    	
    	//Se infla
        MenuInflater inflater = popup.getMenuInflater();
        
        //Se le indica con que se va a inflar, en este caso con ese menu
        inflater.inflate(R.menu.menu_contacto, popup.getMenu());
        
        //Se muestra
        popup.show();
        
        //Este metodo nos permite que item del popupmenu se ha clickado
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
        	
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                	case R.id.action_llamar:
                		llamar(getTelefono(posicion));
                		break;
        			
                	case R.id.action_editar:
                		editarContacto();
                		break;
                	}
                return true;
            }
        });

    }
  
    //Devuelve un string con el numero de telefono de la posicion que pasemos
    private String getTelefono(int posicion){
    	return listaContactos.get(posicion).getTelefono();
    }
    
    //Llama al telefono que le pasemos
    private void llamar(String tel){
    	
    	//Se crea un intent con la accion a realizar. Si se pone ACTION_DIALER aparecera el dialer con el numero pero no llama
    	Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+tel));

    	//Se inicia la actividad
	    startActivity(intent);
    	
    }
    
    private void editarContacto(){
    	
    	//Se crea el atillo para cargar los datos
    	Intent intent=new Intent(this, AnadirContacto.class);
    	
    	//Se crea el bundle donde se meteran los datos
    	Bundle b=new Bundle();
    	b.putString("nombre", listaContactos.get(posicion).getNombre());
    	b.putString("telefono", listaContactos.get(posicion).getTelefono());
    	b.putString("email", listaContactos.get(posicion).getEmail());
    	b.putString("imagen", listaContactos.get(posicion).getImagen());
    	b.putInt("posicion", posicion);
    	
    	//Se mete el bundle en el intent
    	intent.putExtras(b);
    	
    	//se inicia la actividad y se le pone 1 para diferenciarlo de añadir contacto que tiene 0
    	startActivityForResult(intent, 1);
    	
    }
    
    public void cargarListView(){

    	//Se ordena antes de cargar el listview
    	Collections.sort(listaContactos);
    	
    	//Se crea el adaptador que ira dibujando los contactos y se le pasa la lista con los contactos
    	AdaptadorContacto adaptador=new AdaptadorContacto(this, listaContactos);
    	//Se indica cual es el listView que recibira los contactos
		ListView lv=(ListView)findViewById(R.id.listViewAgenda);
		registerForContextMenu(lv);
		lv.setAdapter(adaptador);
    	
    }
    
}