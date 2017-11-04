// cliente.c

#include <stdio.h>
#include <malloc.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <sys/time.h>
#include <string.h>
#include <stddef.h>

#include <stdlib.h>
#include <stdio.h>

int main(int argc, char** argv) {
    int socket_datos;
    int nbytes;
    int salir=0;
    char confirmacion;
    struct sockaddr_in sockname;
    char buffer[128];
    char option[64];
    char * user;
    char * pass;


    char delimiters[] = " ";
    char * processing;
    char * token;

    //char string[50] = "LOGIN Pepe PASS Blanco";
    //char delimiters[] = " ";
    //char * running = string;
    //char * token;

    //token = strsep(&running, delimiters);
    //printf("%s\n", token);
    //token = strsep(&running, delimiters);
    //printf("%s\n", token);    
    //token = strsep(&running, delimiters);
    //printf("%s\n", token);    
    //token = strsep(&running, delimiters);
    //printf("%s\n", token);    

    if (argc != 3){
        perror("Sintaxis: cliente IP_servidor puerto_servidor"),
        exit(1);
    }
	
    char *servidor = argv[1]; // IP SERVIDOR
    int port = atoi(argv[2]); // PORT SERVIDOR

    if((socket_datos=socket(AF_INET, SOCK_STREAM, 0)) == -1)
        perror("Cliente: error en la llamada a la funcion socket"),
        exit(1);

    sockname.sin_family = AF_INET; // FAMILIA DE LA DIRECCION
    sockname.sin_addr.s_addr = inet_addr(servidor); // DIRECCION DE INTERNET
    sockname.sin_port = htons(port); // PUERTO SERVIDOR

    if(connect(socket_datos, (struct sockaddr *) &sockname, sizeof(sockname)) == -1)
        perror("Cliente: error en la llamada de la función connect"),
        exit(1);

    nbytes = recv(socket_datos, buffer, 128, 0);
    if(nbytes == -1)
        perror("Cliente: error Recv (Option Menu)"),
        exit(1);
    
    printf(buffer);
    
    do{
        printf("Elija una opcion: ");
        gets(option);
        
        token = strtok(option, delimiters);

        if(strcmp(token, "LOGIN") == 0){
            token = strtok(NULL, delimiters); 
            if(token != NULL){
                user = token;
                token = strtok(NULL, delimiters);
                if(token != NULL){
                    if(strcmp(token, "PASS") == 0){
                        token = strtok(NULL, delimiters);
                        if(token != NULL){
                            pass = token;
                            strcpy(buffer, "LOGIN ");
                            strcat(buffer, user);
                            strcat(buffer, " ");
                            strcat(buffer, pass);

                            if(send(socket_datos, buffer, 128, 0) == -1)
                                perror("Cliente: error en la llamada a la funcion send (LOGIN)"),
                                exit(1);

                            nbytes = recv(socket_datos, buffer, 20, 0);
                            if(nbytes == -1)
                                perror("Cliente: error Recv (Confirmacion)"),
                                exit(1);
                            
                            printf("\n");

                            if(strcmp(buffer, "OK") == 0){
                                printf("Login correcto.\n");
                                salir = 1;
                            }else{
                                printf("Login incorrecto.\n\n");
                            }
                        }
                    }
                }
            }

        }else if(strcmp(token, "NEW") == 0){
            token = strtok(NULL, delimiters);
            if(token != NULL){
                if(strcmp(token, "USER") == 0){
                    token = strtok(NULL, delimiters);
                    if(token != NULL){
                        user = token;
                        token = strtok(NULL, delimiters);
                        if(token != NULL){
                            if(strcmp(token, "PASS") == 0){
                                token = strtok(NULL, delimiters);
                                if(token != NULL){
                                    pass = token;
                                    strcpy(buffer, "NEW ");
                                    strcat(buffer, user);
                                    strcat(buffer, " ");
                                    strcat(buffer, pass);
        
                                    if(send(socket_datos, buffer, 128, 0) == -1)
                                        perror("Cliente: error en la llamada a la funcion send (NEW USER)"),
                                        exit(1);
                                       
                                    nbytes = recv(socket_datos, buffer, 20, 0);
                                    if(nbytes == -1)
                                        perror("Cliente: error Recv (Confirmacion)"),
                                        exit(1);
                                    
                                    printf("\n");

                                    if(strcmp(buffer, "OK") == 0){
                                        printf("Creación correcta. Estas conectado.\n");
                                        salir = 1;
                                    }else{
                                        printf("Creacion de cliente incorrecta.\n");
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }else if(strcmp(token, "EXIT") == 0){
            strcpy(buffer, "EXIT");

            if(send(socket_datos, buffer, 128, 0) == -1)
                perror("Cliente: error en la llamada a la funcion send (EXIT)"),
                exit(1);
                
            close(socket_datos);
                
            exit(0);
        }
    }while(!salir);    
    salir=0;
    
    nbytes = recv(socket_datos, buffer, 128, 0);
    if(nbytes == -1)
        perror("Cliente: error Recv"),
        exit(1);
    
    printf("\n%s", buffer);
    
    do{
        printf("Elija una opcion: ");
        gets(option);
        
        token = strtok(option, delimiters);

        if(strcmp(token, "GET") == 0){
            token = strtok(NULL, delimiters);
            if(token != NULL){
                if(strcmp(token, "SCORE") == 0){
                    strcpy(buffer, "SCORE");
                    if(send(socket_datos, buffer, 128, 0) == -1)
                        perror("Cliente: error en la llamada a la funcion send (GET SCORE)"),
                        exit(1);
                }
            }
        }else if(strcmp(token, "NEW") == 0){
            token = strtok(NULL, delimiters);
            if(token != NULL){
                if(strcmp(token, "WORD") == 0){
                    strcpy(buffer, "WORD");
                    if(send(socket_datos, buffer, 128, 0) == -1)
                        perror("Cliente: error en la llamada a la funcion send (NEW WORD)"),
                        exit(1);
                }
            }
        }else if(strcmp(token, "EXIT") == 0){
            close(socket_datos);
            exit(0);
        }
    }while(!salir);



    //do{
    //    printf("Teclee el mensaje a transmitir: \n");
    //    gets(buffer);

    //    printf("Has tecleado: %s\n", buffer);

    //    if(send(socket_datos, buffer, 128, 0) == -1)
    //        perror("Cliente: error en la llamada a la funcion send"),
    //        exit(1);

    //}while(strcmp(buffer, "FIN") != 0);

    close(socket_datos);

    exit(0);
}
