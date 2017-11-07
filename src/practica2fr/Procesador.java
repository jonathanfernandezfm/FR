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
import java.util.Random;

public class Procesador extends Thread{
	// Referencia a un socket para enviar/recibir las peticiones/respuestas
	private Socket socketServicio;
	// stream de lectura (por aquí se recibe lo que envía el cliente)
	private DataOutputStream outPrinter;
	// stream de escritura (por aquí se envía los datos al cliente)
	private DataInputStream inReader;
        
        private Random random;
        
        
        File usuarios = null;
        File passwords = null;
        File score = null;
        File palabras = null;
        File traducciones = null;
        FileReader fr = null;
        BufferedReader bufferLecturaFichero = null;
        FileWriter fw = null;
        PrintWriter bufferEscrituraFichero = null;
        
        String cadenaLeidaFichero;
        String cadenaRecibida;
        String nombreJugador;
        String palabraATraducir;
        int[] posJugador = new int[1]; // Indica la posicion de jugador en el fichero users.txt
        int[] posPalabra = new int[1]; // Indica la posicion de la palabra en el fichero palabras.txt
        boolean generarNuevaPalabra = true;
        boolean seguirJugando;
        int opcionRecibida;
        int racha = 1;
        int scoreObtenido;
        boolean exitMenu1 = false;
        boolean exitMenu2 = false;
        boolean exitMenu3 = false;
	
        
        String menu1 = "TRADUCE PALABRAS ENG-ESP\n\n   MENU PRINCIPAL\n**********************\n (1) NEW USER\n (2) LOGIN\n (3) EXIT\n**********************\n";
        String menu2 = "   MENU JUEGO\n**********************\n (1) GET SCORE\n (2) NEW WORD\n (3) EXIT\n**********************\n";
        String menu3 = "**********************\n (1) NEW WORD\n (2) RESOLVE\n (3) BACK\n**********************\n";
	
	// Constructor que tiene como parámetro una referencia al socket abierto en por otra clase
	public Procesador(Socket socketServicio) {
		this.socketServicio=socketServicio;
                random=new Random();
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
                                        nombreJugador = cadenaRecibida;
                                        
                                        // Enviamos por el buffer que el usuario se ha creado correctamente
                                        outPrinter.writeUTF("OK");

                                        // Enviamos la solicitud de nueva contraseña de usuario
                                        outPrinter.writeUTF("Inserta la contraseña del nuevo usuario: ");
                                        
                                        // Leemos la nueva contraseña del nuevo usuario
                                        cadenaRecibida = inReader.readUTF();
                                        
                                        // Escribimos la nueva contraseña en el fichero passwords.txt
                                        insertarNuevaPassword(cadenaRecibida);

                                        // Mostramos que todo ha ido bien
                                        System.out.println("Usuario " + nombreJugador + " creado correctamente");
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
                                    
                                    
                                    // Indica la posicion del nombre de usuario (-1 si no existe) en el fichero users.txt
                                    posJugador[0] = -1;
                                    //Comprobamos si existe y...
                                    if(existeUsuario(cadenaRecibida, posJugador)){
                                        // Guardamos el nombre de usuario 
                                        nombreJugador = cadenaRecibida;                                        
                                        
                                        // Enviamos por el buffer que el usuario existe
                                        outPrinter.writeUTF("OK");

                                        // Enviamos la solicitud de contraseña de usuario
                                        outPrinter.writeUTF("Contraseña: ");
                                        
                                        // Leemos la contraseña del usuario
                                        cadenaRecibida = inReader.readUTF();
                                        
                                        // Comprobamos que la contraseña coincide
                                        if(coincidenPasswords(cadenaRecibida, posJugador)){
                                            // Enviamos por el buffer que el usuario existe
                                            outPrinter.writeUTF("OK");
                                            
                                            // Enviamos el MENU 2
                                            outPrinter.writeUTF(menu2);
                                            
                                            do{
                                                exitMenu3 = false;
                                                opcionRecibida = inReader.readInt();
                                                // Miramos la opcion recibida
                                                switch(opcionRecibida){
                                                    case 1:
                                                        // Obtenemos el escore
                                                        int score = Integer.parseInt(obtenerScore(posJugador));

                                                        // Mandamos el score
                                                        outPrinter.writeUTF("El score de " + nombreJugador + " es: " + score);
                                                    break;
                                                    case 2:
                                                        // Enviar el menu 3
                                                        outPrinter.writeUTF(menu3);
                                                        
                                                        scoreObtenido = 10;
                                                        
                                                        do{
                                                            seguirJugando = jugar();
                                                            if(!seguirJugando)
                                                                exitMenu3 = true;
                                                        }while(!exitMenu3);
                                                    break;
                                                    case 3:
                                                        exitMenu2 = true;
                                                        exitMenu1 = true;
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
                    
                    // Insertamos score a 0
                    fw = new FileWriter(System.getProperty("user.dir")+"/src/practica2fr/score.txt",true);
                    bufferEscrituraFichero = new PrintWriter(fw,true);
                    bufferEscrituraFichero.println("0");
                    
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
        
        // Devuelve el score del jugador del fichero score.txt del jugador en la posicion pos del fichero users.txt
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
        
        // Devuelve la palabra en la posicion posPalabra del fichero palabras.txt
        public String obtenerPalabra(int[] posPalabra){
            String palabra = new String();
            int n = random.nextInt(getNumPalabras());
            
            try{
                // Abrimos el fichero
                palabras = new File(System.getProperty("user.dir")+"/src/practica2fr/palabras.txt");
                fr = new FileReader (palabras);
                bufferLecturaFichero = new BufferedReader(fr);
                
                // Leemos hasta la posicion calculada aleatoriamente
                palabra = bufferLecturaFichero.readLine();
                int i = 0;
                while(i != n){
                    palabra = bufferLecturaFichero.readLine();
                    i++;
                }
                posPalabra[0]=n;

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
            return palabra;
        }
        
        // Devuelve el numero de palabras del fichero palabras.txt
        public int getNumPalabras(){
            int numPalabras = 0;
            try{
                // Abrimos el fichero de palabras
                palabras = new File(System.getProperty("user.dir")+"/src/practica2fr/palabras.txt");
                fr = new FileReader (palabras);
                bufferLecturaFichero = new BufferedReader(fr);
                
                // Leemos todas las palabras
                String linea;
                while((linea=bufferLecturaFichero.readLine())!=null){
                    numPalabras++;
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
            return numPalabras;
        }
        
        // Metodo para obtener la traduccion en la posicion posPalabra del fichero traducciones
        public String obtenerTraduccion(int[] posPalabra){
            String traduccion = new String();
            
            try{
                // Abrimos el fichero
                traducciones = new File(System.getProperty("user.dir")+"/src/practica2fr/traducciones.txt");
                fr = new FileReader (traducciones);
                bufferLecturaFichero = new BufferedReader(fr);
                
                // Leemos hasta la posicion pasada
                traduccion = bufferLecturaFichero.readLine();
                int i = 0;
                while(i != posPalabra[0]){
                    traduccion = bufferLecturaFichero.readLine();
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
            return traduccion;
        }
        
        // Actualiza el score de un jugador en el fichero score.txt
        public void actualizarScore(int[] posJugador, int nuevoScore) throws IOException{
            try{
                String contenido = "";
                
                // Abrimos el fichero
                score = new File(System.getProperty("user.dir")+"/src/practica2fr/score.txt");
                fr = new FileReader (score);
                bufferLecturaFichero = new BufferedReader(fr);
                
                // Leemos todos los scores almacenados
                String linea;
                int i = 0;
                while((linea=bufferLecturaFichero.readLine())!=null){
                    if(i == posJugador[0])
                        contenido += Integer.toString(nuevoScore)+"\n";
                    else
                        contenido += linea+"\n";
                    i++;
                }
                
                // Actualizamos el fichero score.txt con el nuevo contenido

                fw = new FileWriter(System.getProperty("user.dir")+"/src/practica2fr/score.txt");
                bufferEscrituraFichero = new PrintWriter(fw,true);
                    
                // Insertamos el nuevo contenido
                bufferEscrituraFichero.print(contenido);
                    
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
        }
        
        // Metodo para jugar
        public boolean jugar() throws IOException{
            boolean seguirJug = true;
            
            if(generarNuevaPalabra){
                // Escogemos aleatoriamente la palabra a traducir
                palabraATraducir = obtenerPalabra(posPalabra);
                generarNuevaPalabra = false;
            }
            
            // Enviamos la palabra a traducir
            outPrinter.writeUTF("\nPalabra a traducir --> " + palabraATraducir);
            
            // Esperamos la traduccion o una opcion
            cadenaRecibida = inReader.readUTF();
            
            // Obtenemos la traduccion
            String traduccion = obtenerTraduccion(posPalabra);
            
            // NEW WORD
            if("1".equals(cadenaRecibida)){
                generarNuevaPalabra = true;
                outPrinter.writeUTF("\nGenerada nueva palabra a traducir");
            }
            else if("2".equals(cadenaRecibida)){ // RESOLVE
                // Enviamos la traduccion
                outPrinter.writeUTF("La traduccion de " + palabraATraducir + " es: " + traduccion +"\n\nGenerada nueva palabra a traducir");
                generarNuevaPalabra = true;
            }
            else if("3".equals(cadenaRecibida)){ // BACK
                // Enviamos el mensaje de volver al menu 2
                outPrinter.writeUTF("BACK");
                
                generarNuevaPalabra = true;
                seguirJug = false;
            }
            else{ // TRADUCCION ENVIADA
                if(traduccion.equals(cadenaRecibida)){
                    // Incrementamos score en scoreObtenido
                    int nuevoScore = Integer.parseInt(obtenerScore(posJugador)) + scoreObtenido;
                    actualizarScore(posJugador,nuevoScore);
                    
                    // Enviamos mensaje de exito
                    outPrinter.writeUTF("CORRECTO la traduccion de la palabra " + palabraATraducir + " es " + traduccion + "\n\nHas obtenido " + scoreObtenido + " puntos\n\nGenerada nueva palabra a traducir");
                    scoreObtenido = 10;
                    generarNuevaPalabra = true;
                }
                else{
                    scoreObtenido -= 2;
                    if(scoreObtenido > 0){
                        // Enviamos mensaje de error
                        outPrinter.writeUTF("TRADUCCION INCORRECTA. Te quedan " + scoreObtenido/2 + " intentos");
                    }
                    else{
                        scoreObtenido = 10;
                        generarNuevaPalabra = true;
                        // Enviamos mensaje de error
                        outPrinter.writeUTF("TRADUCCION INCORRECTA. Has agotado el numero de intentos\n\nGenerada nueva palabra a traducir");
                    }
                }
            }
                
            return seguirJug;
        }
        
}
