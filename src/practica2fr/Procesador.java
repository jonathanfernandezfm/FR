package practica2fr;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Procesador extends Thread{
	// Referencia a un socket para enviar/recibir las peticiones/respuestas
	private Socket socketServicio;
	// stream de lectura (por aquí se recibe lo que envía el cliente)
	private DataOutputStream outPrinter;
	// stream de escritura (por aquí se envía los datos al cliente)
	private DataInputStream inReader;
        
        
        File usuarios = null;
        File passwords = null;
        File score = null;
        FileReader fr = null;
        BufferedReader bufferLecturaFichero = null;
        FileWriter fw = null;
        PrintWriter bufferEscrituraFichero = null;
        
        String cadenaLeidaFichero;
        String cadenaRecibida;
        String nombre;
        int opcionRecibida;
        boolean exitMenu1 = false;
        boolean exitMenu2 = false;
	
        
        String menu1 = "**********************\n (1) NEW USER\n (2) LOGIN\n (3) EXIT\n**********************\n";
        String menu2 = "**********************\n (1) GET SCORE\n (2) NEW WORD\n (3) EXIT\n**********************\n";
	
	// Constructor que tiene como parámetro una referencia al socket abierto en por otra clase
	public Procesador(Socket socketServicio) {
		this.socketServicio=socketServicio;
	}
	
	
	// Aquí es donde se realiza el procesamiento realmente:
        @Override
	public void run(){
		            
		try {
			// Obtiene los flujos de escritura/lectura
			inReader = new DataInputStream(socketServicio.getInputStream());
			outPrinter = new DataOutputStream(socketServicio.getOutputStream());
			
                        outPrinter.writeUTF(menu1);
                        
                        do{
                            opcionRecibida = inReader.readInt();
                        
                            // Miramos la opcion recibida
                            switch(opcionRecibida){
                                case 1:
                                    // Enviamos la solicitud de nuevo nombre de usuario
                                    outPrinter.writeUTF("Inserta el nombre del nuevo usuario: ");
                                    // Leemos el nombre del nuevo usuario
                                    cadenaRecibida = inReader.readUTF();
                                    if(insertarNuevoUsuario(cadenaRecibida)){
                                        // Guardamos el nombre de usuario para luego mostrarlo
                                        String nuevoNombre = cadenaRecibida;
                                        
                                        // Enviamos por el buffer que el usuario se ha creado correctamente
                                        outPrinter.writeUTF("OK");

                                        // Enviamos la solicitud de nueva contraseña de usuario
                                        outPrinter.writeUTF("Inserta la contraseña del nuevo usuario: ");
                                        
                                        // Leemos la nueva contraseña del nuevo usuario
                                        cadenaRecibida = inReader.readUTF();
                                        
                                        // Escribimos la nueva contraseña en el fichero passwords.txt
                                        insertarNuevaPassword(cadenaRecibida);

                                        // Mostramos que todo ha ido bien
                                        System.out.println("Usuario " + nombre + " creado correctamente");
                                    }
                                    else{
                                        outPrinter.writeUTF("ERROR! Ese nombre de usuario ya existe!");
                                    }

                                break;
                                case 2:
                                    // Enviamos la solicitud de nombre de usuario
                                    outPrinter.writeUTF("LOGIN\nNombre de usuario: ");
                                    // Leemos el nombre del nuevo usuario
                                    cadenaRecibida = inReader.readUTF();
                                    
                                    // Indica la posicion del nombre de usuario (-1 si no existe)
                                    int[] posicion = new int[1];
                                    posicion[0] = -1;
                                    //Comprobamos si existe y...
                                    if(existeUsuario(cadenaRecibida, posicion)){
                                        // Guardamos el nombre de usuario para luego mostrarlo
                                        nombre = cadenaRecibida;                                        
                                        
                                        // Enviamos por el buffer que el usuario existe
                                        outPrinter.writeUTF("OK");
                                        
                                        // Enviamos la solicitud de contraseña de usuario
                                        outPrinter.writeUTF("Contraseña: ");
                                        
                                        // Leemos la contraseña del usuario
                                        cadenaRecibida = inReader.readUTF();
                                        
                                        // Comprobamos que la contraseña coincide
                                        if(coincidenPasswords(cadenaRecibida, posicion)){
                                            // Enviamos por el buffer que el usuario existe
                                            outPrinter.writeUTF("OK");
                                            
                                            // Enviamos el MENU 2
                                            outPrinter.writeUTF(menu2);
                                            
                                            do{
                                                opcionRecibida = inReader.readInt();
                                                System.out.print("menu 2");
                                                // Miramos la opcion recibida
                                                switch(opcionRecibida){
                                                    case 1:
                                                        posicion[0] = -1;
                                                        if(existeUsuarioScore(nombre, posicion)){
                                                            // Enviamos por el buffer que el usuario existe
                                                            outPrinter.writeUTF("OK");
                                                            
                                                            int score = Integer.parseInt(obtenerScore(posicion));
                                                            
                                                            outPrinter.writeUTF("El score de " + nombre + " es: " + score);
                                                        }
                                                        else{
                                                            outPrinter.writeUTF("ERROR");
                                                            outPrinter.writeUTF("ERROR! El usuario " + nombre + " no tiene score!\n");
                                                        }
                                                    break;
                                                    case 2:
                                                    case 3:
                                                        exitMenu2 = true;
                                                    break;
                                                }
                                            }while(!exitMenu2);
                                        }
                                        else{
                                            outPrinter.writeUTF("ERROR! La contraseña no coincide!");
                                        }
                                    }
                                    else{
                                        outPrinter.writeUTF("ERROR! El usuario " + cadenaRecibida + " no existe!");
                                    }
                                break;
                                case 3:
                                    exitMenu1 = true;
                                break;
                            }
                        }while(!exitMenu1);
                        
                        
                        
                        
                        socketServicio.close();
			
			
		} catch (IOException e) {
			System.err.println("Error al obtener los flujso de entrada/salida.");
		}

	}
        
        // Devuelve si puede insertarse el nuevo usuario
        public boolean insertarNuevoUsuario(String nombreUsuario){
            boolean puedeInsertarse = true;
            boolean insertado = false;
            try{
                // Abrimos el fichero
                usuarios = new File(System.getProperty("user.dir")+"/src/practica2fr/users.txt");
                fr = new FileReader (usuarios);
                bufferLecturaFichero = new BufferedReader(fr);
                
                // Leemos todos los usuarios que haya en el fichero
                String linea;
                while((linea=bufferLecturaFichero.readLine())!=null && puedeInsertarse)
                    if(linea.equals(nombreUsuario))
                        puedeInsertarse = false;
                
                // Si puede insertarse lo insertamos
                if(puedeInsertarse){
                    // Abrimos el fichero
                    fw = new FileWriter(System.getProperty("user.dir")+"/src/practica2fr/users.txt",true);
                    bufferEscrituraFichero = new PrintWriter(fw,true);
                    
                    // Insertamos el nuevo nombre
                    bufferEscrituraFichero.println(nombreUsuario);
                    insertado = true;
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }finally{
                // En el finally cerramos el fichero, para asegurarnos
                // que se cierra tanto si todo va bien como si salta 
                // una excepcion.
                try{                    
                   if( null != fr ){   
                      fr.close();
                   }
                   if( null != fw ){   
                      fw.close();
                   }             
                }catch (Exception e2){ 
                   e2.printStackTrace();
                }
             }
            
            return insertado;
        }
        
        // Inserta password de usuario en el fichero passwords.txt
        public void insertarNuevaPassword(String passwordUsuario){
            try{
                fw = new FileWriter(System.getProperty("user.dir")+"/src/practica2fr/passwords.txt",true);
                bufferEscrituraFichero = new PrintWriter(fw,true);

                // Insertamos el nuevo nombre
                bufferEscrituraFichero.println(passwordUsuario);
            }
            catch(Exception e){
                e.printStackTrace();
            }finally{
                // En el finally cerramos el fichero, para asegurarnos
                // que se cierra tanto si todo va bien como si salta 
                // una excepcion.
                try{                    
                   if( null != fw ){   
                      fw.close();
                   }             
                }catch (Exception e2){ 
                   e2.printStackTrace();
                }
             }
        }
        
        // Comprueba si un usuario existe
        public boolean existeUsuario(String nombreUsuario, int[] posicion){
            boolean existe = false;
            try{
                // Abrimos el fichero
                usuarios = new File(System.getProperty("user.dir")+"/src/practica2fr/users.txt");
                fr = new FileReader (usuarios);
                bufferLecturaFichero = new BufferedReader(fr);
                
                // Leemos todos los usuarios que haya en el fichero
                String linea;
                int i = 0;
                while((linea=bufferLecturaFichero.readLine())!=null && !existe){
                    if(linea.equals(nombreUsuario))
                        existe = true;
                    else
                        i++;
                }
                
                if(existe)
                    posicion[0] = i;
                
            }
            catch(Exception e){
                e.printStackTrace();
            }finally{
                // En el finally cerramos el fichero, para asegurarnos
                // que se cierra tanto si todo va bien como si salta 
                // una excepcion.
                try{                    
                   if( null != fr ){   
                      fr.close();
                   }
                }catch (Exception e2){ 
                   e2.printStackTrace();
                }
             }
            
            return existe;
        }
        
        // Comprueba si un usuario con score existe
        public boolean existeUsuarioScore(String nombreUsuario, int[] posicion){
            boolean existe = false;
            try{
                // Abrimos el fichero
                usuarios = new File(System.getProperty("user.dir")+"/src/practica2fr/user_score.txt");
                fr = new FileReader (usuarios);
                bufferLecturaFichero = new BufferedReader(fr);
                
                // Leemos todos los usuarios que haya en el fichero
                String linea;
                int i = 0;
                while((linea=bufferLecturaFichero.readLine())!=null && !existe){
                    if(linea.equals(nombreUsuario))
                        existe = true;
                    else
                        i++;
                }
                
                if(existe)
                    posicion[0] = i;
                
            }
            catch(Exception e){
                e.printStackTrace();
            }finally{
                // En el finally cerramos el fichero, para asegurarnos
                // que se cierra tanto si todo va bien como si salta 
                // una excepcion.
                try{                    
                   if( null != fr ){   
                      fr.close();
                   }
                }catch (Exception e2){ 
                   e2.printStackTrace();
                }
             }
            
            return existe;
        }
        
        // Comprueba si la password pasada coincide con la del fichero passwords.txt en la posicion pos
        public boolean coincidenPasswords(String passwordUsuario, int[] pos){
            boolean coinciden = false;
            try{
                // Abrimos el fichero
                passwords = new File(System.getProperty("user.dir")+"/src/practica2fr/passwords.txt");
                fr = new FileReader (passwords);
                bufferLecturaFichero = new BufferedReader(fr);
                
                // Leemos hasta la posicion de la password
                String linea = bufferLecturaFichero.readLine();
                int i = 0;
                while(i != pos[0]){
                    linea = bufferLecturaFichero.readLine();
                    i++;
                }
                
                // Comparamos la password del fichero con la pasada por el usuario
                if(linea.equals(passwordUsuario))
                    coinciden = true;
                
            }
            catch(Exception e){
                e.printStackTrace();
            }finally{
                // En el finally cerramos el fichero, para asegurarnos
                // que se cierra tanto si todo va bien como si salta 
                // una excepcion.
                try{                    
                   if( null != fr ){   
                      fr.close();
                   }
                }catch (Exception e2){ 
                   e2.printStackTrace();
                }
             }
            
            return coinciden;
        }
        public String obtenerScore(int[] pos){
            String linea = new String();
            try{
                // Abrimos el fichero
                score = new File(System.getProperty("user.dir")+"/src/practica2fr/score.txt");
                fr = new FileReader (score);
                bufferLecturaFichero = new BufferedReader(fr);
                
                // Leemos hasta la posicion de la password
                linea = bufferLecturaFichero.readLine();
                int i = 0;
                while(i != pos[0]){
                    linea = bufferLecturaFichero.readLine();
                    i++;
                }

            }
            catch(Exception e){
                e.printStackTrace();
            }finally{
                // En el finally cerramos el fichero, para asegurarnos
                // que se cierra tanto si todo va bien como si salta 
                // una excepcion.
                try{                    
                   if( null != fr ){   
                      fr.close();
                   }
                }catch (Exception e2){ 
                   e2.printStackTrace();
                }
             }
            return linea;
        }
}
