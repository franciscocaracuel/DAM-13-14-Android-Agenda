package com.izv.agenda;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AnadirContacto extends Activity {
	
	private int RESULTADO_CAMARA=0, RESULTADO_GALERIA=1;

	private String nombre, telefono, email, imagen="defecto";
	//Se inicia en -1 para que no tenga problemas con editar
	private int posicion=-1;
	private ArrayList<Contacto> listaContactos;
	private EditText etNombre;
	private EditText etTelefono;
	private EditText etEmail;
	private Button btAnadir;
	private Bundle bundle;
	private boolean editar=false;
	private ImageView ivContacto;
	private Uri imagenSeleccionada;
	private Bitmap bitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anadir_contacto);
		
		inicio();
		
	}
	
	//Guarda
    @Override
    protected void onSaveInstanceState(Bundle savingInstanceState) {
    	
    	super.onSaveInstanceState(savingInstanceState);
    	
    	//Bitmap implementa la clase parcelable, por eso la podemos pasar con putParcelable
    	savingInstanceState.putParcelable("imagen", bitmap);
    	savingInstanceState.putString("imagenString", imagen);
    	
    }
    
    //Recupera
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	
    	super.onRestoreInstanceState(savedInstanceState);

    	//Se recoge la imagen y se coloca en el imageView
    	bitmap=savedInstanceState.getParcelable("imagen");
    	ivContacto.setImageBitmap(bitmap);
    	
    	imagen=savedInstanceState.getString("imagenString");
    	
    }
	
	public void inicio(){
				
		//Se inicializa para que no de problema al guardar la instancia
		imagenSeleccionada=Uri.parse("");
		
		//////INICIALIZAMOS NUESTRAS VARIABLES//////////
		
		//Se establecen los editText
		etNombre=(EditText)findViewById(R.id.etNombre);
		etTelefono=(EditText)findViewById(R.id.etTelefono);
		etEmail=(EditText)findViewById(R.id.etEmail);
		
    	//Se define imagen del contacto
    	ivContacto=(ImageView)findViewById(R.id.ivContacto);
			
		//Se iguala el arraylist principal con el de esta clase
		this.listaContactos=MainActivity.getListaContactos();
			
		//////////////////////////////////////////////////
		
		
		//Se crea el bundle para recoger los datos que vengan si vienen
		bundle=getIntent().getExtras();
				
		//El try catch es para saber si se edita un contacto o se añade
		//Si existe "nombre" en el bundle significa que se edita, si da error y no hay nada es que se añade por tanto ocultamos 
		//el layout que tiene los iconos de llamar, etc.
		try{
					
			nombre=bundle.getString("nombre");

			editar();
					
		} catch(NullPointerException error){
			
			RelativeLayout rl=(RelativeLayout)findViewById(R.id.RLImageView);
			rl.setVisibility(View.INVISIBLE);
			
		}		
		
	}
	
	public void anadirOEditar(View v){
		
		//Al pulsar boton anadir debemos saber si se edita o se añade. como editar se inicia en false si hay que añadir editar valdra false
		if(!editar){
			
			anadir();
			
		} else{
			
			actualizar();
			
		}
		
	}
	
	public void anadir(){
				
		//Si los datos han sido correctos podemos crear el objeto Contacto
		if(validar()){
			
			//Se crea un objeto auxiliar con los nuevos datos
			Contacto cAux=new Contacto(nombre, telefono, email, imagen);
						
			//Si no existe se enviara a la otra activity un array con los datos. listaContactos es static por tanto se podria aprovechar
			//e insertar ya el objeto pero para aprovechar el intent lo vamos a hacer asi
			//Si existe se sacara una tostada avisando
			if(existeContacto(cAux)==0){
				
				ArrayList<String> listaString=new ArrayList<String>();
				listaString.add(nombre);
				listaString.add(telefono);
				listaString.add(email);
				listaString.add(imagen);
				
				Intent intencion=new Intent();
				Bundle b=new Bundle();
				b.putStringArrayList("listaContactos", listaString);
				intencion.putExtras(b);
				setResult(Activity.RESULT_OK, intencion);
				finish();
				
			} else{
				
				Toast.makeText(this, R.string.info_contacto_existe, Toast.LENGTH_SHORT).show();
								
			}
			
		} else{
			
			Toast.makeText(this, R.string.info_contacto_invalido, Toast.LENGTH_SHORT).show();
						
		}
		
	}
	
	//Coloca los datos en los edittext y saca la posicion a editar
	public void editar(){
		
		//El boolean editar se pone a true para que sepa esto que se edita
		editar=true;
				
		//nombre ya sabemos que contiene algo		
		etNombre.setText(nombre);
		
		//Telefono seguro que tendra algo porque no se puede dejar vacio
		telefono=bundle.getString("telefono");
		etTelefono.setText(telefono);
		
		//Como el email si puede estar vacio se comprueba si esta vacio. Si lo esta el editText se dejara en blanco
		try{
			email=bundle.getString("email");
			etEmail.setText(email);
		}catch(NullPointerException error){
			email="";
			etEmail.setText("");
		}
		
		imagen=bundle.getString("imagen");
		//Se le pone la imagen
		if(!imagen.equals("defecto")){			

			InputStream stream = null;
            try {
            	
                stream = new FileInputStream(imagen);
                
                //Si se ha borrado el archivo dejara la imagen por defecto
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
		
		//Para saber la posicion del elemento a editar la obtenemos
		posicion=bundle.getInt("posicion");
		
		//Se le cambia el nombre al boton anadir y se le pone editar
		btAnadir=(Button)findViewById(R.id.btAnadir);
		btAnadir.setText(R.string.bt_actualizar);
		
	}
	
	//La segunda parte del editar, esto se ejecuta al darle al boton anadir
	public void actualizar(){
		
		if(validar()){
			
			//Creamos el objeto Contacto con los datos actualizados
			Contacto cAux=new Contacto(nombre, telefono, email, imagen);
			
			//Si no existe o es el mismo se edita el objeto que hemos pulsado
			if(existeContacto(cAux)==0 || existeContacto(cAux)==-1){
				
				cAux=listaContactos.get(posicion);
				cAux.setNombre(nombre);
				cAux.setTelefono(telefono);
				cAux.setEmail(email);
				cAux.setImagen(imagen);
				
				MainActivity.setListaContactos(listaContactos);
				
				Intent intent=new Intent();
				setResult(Activity.RESULT_OK, intent);
				finish();
				
			//Si existe pero es otro no deja actualizar el contacto
			} else if(existeContacto(cAux)==1){
				
				Toast.makeText(this, R.string.info_contacto_existe, Toast.LENGTH_SHORT).show();		
			
			}
			
		} else{
			
			Toast.makeText(this, R.string.info_contacto_invalido, Toast.LENGTH_SHORT).show();
			
		}
		
	}
	
	public void cancelar(View v){
		
		Intent intencion=new Intent();
		setResult(Activity.RESULT_CANCELED, intencion);
		finish();
		
	}
	
	//Devuelve true si nombre y telefono estan rellenos
	public boolean validar(){
		
		//Se guardan en las variables el string que corresponde		
		nombre=etNombre.getText().toString();
		telefono=etTelefono.getText().toString();
		email=etEmail.getText().toString();
				
		//Se validan los datos
		if(nombre.equals("") || telefono.equals("")){
			etNombre.setHintTextColor(Color.RED);
			etTelefono.setHintTextColor(Color.RED);
			return false;
		}
		
		return true;
		
	}
	
	//Devuelve 1 si existe en la lista el contacto que le pasamos
	//Devuelve 0 si no existe
	//Devuelve -1 si ademas es él mismo el que se encuentra (lo usamos para editar)
	public int existeContacto(Contacto cAux){
	
		//Se comprueba que no exista ya el contacto
		for(int i=0;i<listaContactos.size();i++){
			
			//Si existe el contacto se comprueba si es el mismo que el que se edita o no
			if(listaContactos.get(i).equals(cAux)){
				
				//Si el contador se ha parado en la misma posicion que en la posicion del listado significa que 
				//estamos editando el mismo objeto
				//Si no pues es otro objeto diferente
				if(posicion==i){					
					return -1;					
				} else{
					return 1;
				}
				
			}
			
		}
		
		//Si no ha entrado en ningun return significa que no existe
		return 0;
		
	}
	
	public void ivEliminar(View v){
				
		//Se crea un dialogo para confirmar el borrado
		AlertDialog.Builder ad = new AlertDialog.Builder(this);  
		ad.setTitle(R.string.dialogo_mensaje_borrar_texto);  
		//ad.setMessage(""); Esto añadiria un mensaje debajo del titulo
		
		//Si se pulsa en ok se borra
		ad.setPositiveButton(R.string.dialogo_mensaje_ok, new DialogInterface.OnClickListener() {  
		   public void onClick(DialogInterface dialog, int id) {  
			   
			   //Como tenemos la posicion se coge el arraylist y se borra esa posicion
			   listaContactos.remove(posicion);
			   
			   //Nos vamos al principal diciendo que se ha terminado la edicion
			   Intent intent=new Intent();
			   //Le pongo 100 para yo saber que se esta eliminando, se que si devuelve ok es -1 y que si cancela es 0
			   setResult(100, intent);
			   finish();
			   
		   }  
		});  
		
		//Si se pulsa en cancelar nada
		ad.setNegativeButton(R.string.dialogo_mensaje_cancelar, new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int id) {  
				dialog.cancel(); 
			}  
		}); 
		
		//Se muestra el dialogo
		ad.show(); 
		
	}
	
	public void ivLlamar(View v){
		
		llamar(telefono);
		
	}
	
	public void ivGmail(View v){
		
		//Si el email no estaba vacio al darle a editar
		if(!email.equals("")){
			
			//Se cre ael intent con el tema para iniciar el gmail
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"+email));
	        startActivity(intent);
	        
		} else{
			
			Toast.makeText(this, R.string.info_no_email, Toast.LENGTH_SHORT).show();
			
		}
				
	}
	
	public void ivSms(View v){
		
		//Se crea el intent con el tema para iniciar el sms y se inicia la actividad
		Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"+telefono)); 
		startActivity(sendIntent);		
		
	}
	
	//Llama al telefono que le pasemos
    private void llamar(String tel){
    	
    	//Se crea un intent con la accion a realizar. Si se pone ACTION_DIALER aparecera el dialer con el numero pero no llama
    	Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+tel));

    	//Se inicia la actividad
	    startActivity(intent);
    	
    }
    
    public void guardarFoto(View v){
		
		// Se crea un dialogo para confirmar el borrado
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		ad.setTitle(R.string.dialogo_mensaje_seleccionar_imagen);
		// ad.setMessage(""); Esto añadiria un mensaje debajo del titulo

		// Abre la camara y espera el resultado
		ad.setPositiveButton(R.string.dialogo_mensaje_camara,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						
						startActivityForResult(intent, RESULTADO_CAMARA);

					}
				});

		// Abre la galeria y espera el resultado
		ad.setNegativeButton(R.string.dialogo_mensaje_galeria,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						
						Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
					    intent.setType("image/*");
					    startActivityForResult(intent, RESULTADO_GALERIA);
						
					}
				});

		// Se muestra el dialogo
		ad.show();
				
	}    
    
    /////////////////////////////RECOGEMOS EL ATILLO PARA LAS FOTOS////////////////////////////////
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	    	
    	super.onActivityResult(requestCode, resultCode, data);
    			
    	//Si hemos llamado a la camara entrara aqui
    	if(requestCode == RESULTADO_CAMARA){
    			
    		if(resultCode == Activity.RESULT_OK){
    			
    			//Como queremos la vista previa de la imagen cogemos el extra data del intent
    			//y se muestra en el imageView
    			if (data.hasExtra("data")) {
    				
    				//getdata nos devuelve la uri de la imagen
    				imagenSeleccionada = data.getData();
    				
    				//Todo lo siguiente saca la imagen
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(imagenSeleccionada, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
    				
                    //Consigue el bitmap
    				bitmap=(Bitmap)data.getParcelableExtra("data");
    			    ivContacto.setImageBitmap(bitmap);
    			    
    			    //Se guarda en imagen para poder cargarla cuando haga falta
    			    imagen=filePath;
    			    
    			}
    		    
    		}
    			
    	}

    	//Si hemos abierto la galeria haremos lo que hay aqui
    	if (requestCode == RESULTADO_GALERIA){
    			
    		if (resultCode == Activity.RESULT_OK) {
    			
    			try{
    				
    				//getdata nos devuelve la uri de la imagen
    				imagenSeleccionada = data.getData();
    				
    				//Todo lo siguiente saca la imagen
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(imagenSeleccionada, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    
                    InputStream stream;
					try {
						
						stream = new FileInputStream(filePath);
						BitmapFactory.Options options=new BitmapFactory.Options();
	                    options.inSampleSize = 8;
	                    bitmap = BitmapFactory.decodeStream(stream, null,options);
	                    ivContacto.setImageBitmap(bitmap);
	                    
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						
						e.printStackTrace();
					}
                    
                    //Se mete en imagen para recuperarlo despues
                    //imagen=imagenSeleccionada.toString();
                    imagen=filePath;
    				
                //Si falla la carga mostrara un mensaje
    			} catch(OutOfMemoryError error){
    				Toast.makeText(this, R.string.error_cargar_imagen, Toast.LENGTH_SHORT).show();
    			}    			
    			
    		} 
    			
    	}
    	
    }  

}