package practica2fr;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Cliente {

        static Scanner in = new Scanner(System.in);
        static String cadenaEnviada;
        static String menu1 = new String();
        static String menu2 = new String();
        static String cadenaRecibida;
        static int opcionSelec = -1;
        static boolean exitMenu1 = false;
        static boolean exitMenu2 = false;
        static DataInputStream inReader = null;
        static DataOutputStream outPrinter = null;
    
	public static void main(String[] args) {
		
                System.out.println(System.getProperty("user.dir"));
            
		// Nombre del host donde se ejecuta el servidor:
		String host="localhost";
		// Puerto en el que espera el servidor:
		int port=8989;
		
		// Socket para la conexión TCP
		Socket socketServicio=null;
		
		try {
			// Creamos un socket que se conecte a "hist" y "port":
			socketServicio = new Socket(host,port);

                        // Creamos los buffer de escritura y de lectura
                        inReader = new DataInputStream(socketServicio.getInputStream());
                        outPrinter = new DataOutputStream(socketServicio.getOutputStream());
                        
                        // Leemos el menu 1 del buffer
                        menu1 = inReader.readUTF();
                        menu1 = "\n"+menu1;
                        
                        limpiarVentana();
                        
                        do{
                            // Recogemos por terminal la opcion deseada del MENU 1
                            do{ 
                                // Mostramos el menu1 (NEW USER, LOGIN, EXIT)
                                System.out.println(menu1);

                                try {
                                    System.out.print("Introduzca una opcion: ");
                                        opcionSelec = in.nextInt();

                                } catch (InputMismatchException e) {
                                    // Limpiamos la ventana
                                    limpiarVentana();
                                    System.out.println("\n\nERROR! Debes insertar un número");
                                    in.next();
                                }  

                            }while(!opcionMenuValida(opcionSelec));
                            
                            // Limpiamos el terminal
                            limpiarVentana();
                            
                            // Enviamos la opcion seleccionada en el MENU 1
                            if(opcionSelec == 1 || opcionSelec == 2){
                                // Enviamos la opcion seleccionada por el buffer
                                outPrinter.writeInt(opcionSelec);
                                // Recibimos el mensaje de respuesta (NEW USER o LOGIN USER)
                                cadenaRecibida = inReader.readUTF();
                                
                                // NEW USER
                                if(opcionSelec == 1){
                                    opcionNewUser();
                                }
                                else{ // LOGIN USER
                                    if(opcionLoginUser()){                                        
                                        do{
                                            // Recogemos por terminal la opcion deseada del MENU 2
                                            do{
                                                System.out.println(menu2);
                                                
                                                try {
                                                    System.out.print("Introduzca una opcion: ");
                                                        opcionSelec = in.nextInt();

                                                } catch (InputMismatchException e) {
                                                    // Limpiamos la ventana
                                                    limpiarVentana();
                                                    System.out.println("\n\nERROR! Debes insertar un número");
                                                    in.next();
                                                }

                                                limpiarVentana();

                                            }while(!opcionMenuValida(opcionSelec));
                                                                               
                                            // Enviamos la opcion seleccionada en el MENU 1
                                            if(opcionSelec == 1 || opcionSelec == 2){
                                                // Enviamos la opcion seleccionada por el buffer
                                                outPrinter.writeInt(opcionSelec);
                                                //Recibimos el mensaje de respuesta
                                                cadenaRecibida = inReader.readUTF();
                                                
                                                // SCORE
                                                if(opcionSelec == 1){
                                                    opcionScore();
                                                }
                                                else{ // GET WORD
                                                    
                                                }
                                            }
                                            else{
                                                outPrinter.writeInt(opcionSelec);
                                                exitMenu2 = true;
                                            }
                                            
                                        }while(!exitMenu2);
                                    }
                                    else{
                                        System.out.println("ERROR! Login erroneo");
                                    }
                                }
                            }
                            else{ // Si es un 3 salimos
                                outPrinter.writeInt(opcionSelec);
                                exitMenu1 = true;
                            }                         
                        }while(!exitMenu1 && !exitMenu2);
                              
                        System.out.println("Hasta luego :)");
                        
			// Una vez terminado el servicio, cerramos el socket (automáticamente se cierran
			// el inpuStream  y el outputStream)
			socketServicio.close();
			
                // Excepciones:
		} catch (UnknownHostException e) {
			System.err.println("Error: Nombre de host no encontrado.");
		} catch (IOException e) {
			System.err.println("Error de entrada/salida al abrir el socket.");
		}
	}
        
        // Controla la opcion seleccionada del menu 1
        public static boolean opcionMenuValida(int opcionSelec){
            return (opcionSelec == 1 || opcionSelec == 2 || opcionSelec == 3);
        }
        
        public static void limpiarVentana(){
            for(int i= 0; i < 100; i++)
                System.out.println();
        }
        
        public static void opcionNewUser() throws IOException{
            // Mostramos el mensaje de instroducir nombre de nuevo usuario
            System.out.print(cadenaRecibida);
            // Solicitamos el nombre de nuevo usuario
            cadenaEnviada = in.next();
            // Enviamos el nombre de usuario
            outPrinter.writeUTF(cadenaEnviada);

            // Recibimos el mensaje de si el usuario se ha creado correctamente
            cadenaRecibida = inReader.readUTF();
            
            // Si se ha creado...
            if("OK".equals(cadenaRecibida)){
                // Recibimos el mensaje de meter password y lo mostramos
                cadenaRecibida = inReader.readUTF();
                System.out.print(cadenaRecibida);

                // Solicitamos la contraseña del nuevo usuario
                cadenaEnviada = in.next();
                
                // Enviamos la constraseña del usuario
                outPrinter.writeUTF(cadenaEnviada);

                // Mostramos que todo se ha realizado correctamente
                System.out.println("Usuario + Password creado correctamente");

            }
            else{
                System.out.println(cadenaRecibida);
            }
        }
        
        public static boolean opcionLoginUser() throws IOException{
            boolean logueadoCorrectamente = false;
            // Mostramos el mensaje de instroducir nombre usuario
            System.out.print(cadenaRecibida);
            // Solicitamos el nombre de usuario
            cadenaEnviada = in.next();
            // Enviamos el nombre de usuario
            outPrinter.writeUTF(cadenaEnviada);

            // Recibimos el mensaje de si el usuario existe
            cadenaRecibida = inReader.readUTF();
            
            // Si existe...
            if("OK".equals(cadenaRecibida)){
                // Recibimos el mensaje de meter password y lo mostramos
                cadenaRecibida = inReader.readUTF();
                System.out.print(cadenaRecibida);

                // Solicitamos la contraseña del usuario
                cadenaEnviada = in.next();
                
                // Enviamos la constraseña del usuario
                outPrinter.writeUTF(cadenaEnviada);

                // Recibimos el mensaje de si la password coincide
                cadenaRecibida = inReader.readUTF();

                // Si coincide...
                if("OK".equals(cadenaRecibida)){
                    // Limpiamos la ventana
                    limpiarVentana();
                    
                    // Mostramos que todo se ha realizado correctamente
                    System.out.println("Login completado correctamente");

                    logueadoCorrectamente = true;
                    
                    // Recibimos el menu 2
                    menu2 = inReader.readUTF();
                    menu2 = "\n"+menu2;
                    
                     
                }
                else{
                    // La contraseña no coincide
                    System.out.println(cadenaRecibida);
                }
            }
            else{
                // El nombre de usuario no coincide
                System.out.println(cadenaRecibida);
            }
            return logueadoCorrectamente;
        }
        
        public static void opcionScore() throws IOException{
            // Recibimos el score
            cadenaRecibida = inReader.readUTF();
            // Imprimimos el score
            System.out.println(cadenaRecibida);
        }
}
