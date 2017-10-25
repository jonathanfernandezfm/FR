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
        perror("Servidor: error Recv"),
        exit(1);
    
    printf(buffer);
    
    do{
        printf("Elija una opcion\n");
        gets(option);
        //printf("%s", option);
        
        processing = option;
        token = strsep(&processing, delimiters);

        if(strcmp(token, "LOGIN") == 0){
            token = strsep(&processing, delimiters); 
            if(token != 0){
                user = token;
                printf("User: %s\n", user);
                token = strsep(&processing, delimiters);
                if(token != 0){
                    if(strcmp(token, "PASS") == 0){
                        token = strsep(&processing, delimiters);
                        if(token != 0){
                            pass = token;
                            printf("Pass: %s\n", token);
                        }
                    }
                }
            }
        }else if(strcmp(token, "NEW") == 0){
            
        }else if(strcmp(token, "EXIT") == 0){

        }
    }while(1);

    
    

    do{
        printf("Teclee el mensaje a transmitir: \n");
        gets(buffer);

        printf("Has tecleado: %s\n", buffer);

        if(send(socket_datos, buffer, 80, 0) == -1)
            perror("Cliente: error en la llamada a la funcion send"),
            exit(1);
    }while(strcmp(buffer, "FIN") != 0);

    close(socket_datos);

    exit(0);
}
