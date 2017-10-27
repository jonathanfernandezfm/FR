// servidor.c

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

int main(int argc, char** argv) {
    int socket_control, from_len, socket_datos;
    int nbytes;
    struct sockaddr_in from, sockname;
    char buffer[128];
    char confirmacion[20];    
    char optionMenu[128] = "**********************\n - NEW USER * PASS *\n - LOGIN * PASS *\n - EXIT\n**********************\n";
    char gameMenu[128] = "**********************\n - GET SCORE\n - NEW WORD\n - EXIT\n**********************\n";
    char playingMenu[128] = "**********************\n - WORD *\n - RESOLVE\n - NEW WORD\n - EXIT\n**********************\n";
    char * user;
    char * pass;
    int salir = 0;
    int correcto = 0;

    FILE *users;
    char line[100];
    int endof = 1;
    if ((users = fopen("users.txt","r")) == NULL){
        printf("Error! opening file");
        exit(1);
    }

    char delimiters[] = " ";
    char * processing;
    char * token;

    if(argc != 2)
        perror("Sintaxis: servidor puerto_servidor"),
        exit(1);

    int port = atoi(argv[1]);

    if((socket_control = socket(AF_INET, SOCK_STREAM, 0)) == -1)
        perror("Servidor: error en la llamada a la funcion socket"),
        exit(1);

    sockname.sin_family = AF_INET;
    sockname.sin_addr.s_addr = INADDR_ANY;
    sockname.sin_port = htons(port);

    if(bind(socket_control, (struct sockaddr *) &sockname, sizeof(sockname)) == -1)
        perror("Servidor: error en la llamada a la funcion bind"),
        exit(1);

    if(listen(socket_control,1) == -1)
        perror("Servidor: error en la llamada a la funcion listen"),
        exit(1);

    do{
        from_len = sizeof(from);
        socket_datos = accept(socket_control, (struct sockaddr *) &from, &from_len);
        if(socket_datos == -1)
            perror("Servidor: error en la llamada a al funcion accept"),
            exit(1);

        if(send(socket_datos, optionMenu, 128, 0) == -1)
            perror("Servidor: error en la llamada a la funcion send"),
            exit(1);
        printf("Conexión establecida. Option Menu\n");

        do{
            nbytes = recv(socket_datos, buffer, 128, 0);
            if(nbytes == -1)
                perror("Servidor: error Recv (Option Selected)"),
                exit(1);

            if(nbytes == 0)
                perror("El cliente se ha desconectado"),
                exit(1);

            processing = buffer;
            token = strsep(&processing, delimiters);
    
            if(strcmp(token, "LOGIN") == 0){
                token = strsep(&processing, delimiters); 
                user = token;
                token = strsep(&processing, delimiters);
                pass = token;
                
                do {
                    fgets(line, 100, (FILE *) users);

                    processing = line;
                    token = strsep(&processing, delimiters);
                    
                    if(strcmp(user, token) == 0){
                        token = strsep(&processing, delimiters);
                        if(token != 0){
                            if(feof(users) == 0){
                                strcat(pass, "\n");
                            }
                            if(strcmp(pass, token) == 0){
                                correcto = 1;
                            }else{

                            }
                        }
                    }else{

                    }
                }while(feof(users) == 0 && !correcto);

                if(correcto){
                    strcpy(confirmacion, "OK"); // SI ES CORRECTO
                    printf("Login correcto. Game Menu");
                    if(send(socket_datos, confirmacion, 20, 0) == -1)
                        perror("Servidor: error en la llamada a la funcion send (Confirmación LOGIN)"),
                        exit(1);
                    salir = 1;
                }else{
                    strcpy(confirmacion, "ERR"); // SI NO ES CORRECTO
                    printf("Login incorrecto.");
                    if(send(socket_datos, confirmacion, 20, 0) == -1)
                        perror("Servidor: error en la llamada a la funcion send (Confirmación LOGIN)"),
                        exit(1);
                    rewind(users);
                }
                

            }else if(strcmp(token, "NEW") == 0){
                token = strsep(&processing, delimiters);
                user = token;
                token = strsep(&processing, delimiters);
                pass = token;
                
                do {
                    endof =(int) fgets(line, 100, (FILE *) users);
                    printf("%s %i\n", line, endof);
                }while(endof != 0);

                strcpy(confirmacion, "OK"); // AÑADIR USUARIO SI ES CORRECTO
                printf("Creacion correcta. Game Menu");                
                if(send(socket_datos, confirmacion, 20, 0) == -1)
                    perror("Servidor: error en la llamada a la funcion send (Confirmacion NEW)"),
                    exit(1);
                salir = 1;
            }else if(strcmp(token, "EXIT") == 0){
                salir = 1;
            }
            printf("\n");
        }while(!salir);
        salir = 0;
        if(send(socket_datos, gameMenu, 128, 0) == -1)
            perror("Servidor: error en la llamada a la funcion send (gameMenu)"),
            exit(1);

        do{
            nbytes = recv(socket_datos, buffer, 128, 0);
            if(nbytes == -1)
                perror("Servidor: error Recv (Option Selected)"),
                exit(1);

            if(nbytes == 0)
                perror("El cliente se ha desconectado."),
                exit(1);
        
            if(strcmp(buffer, "SCORE") == 0){
                // PROCESO DE MOSTRAR SCORE
            }else if(strcmp(buffer, "WORD") == 0){
                // PROCESO DE DEVOLVER PALABRA
            }
            
            
            printf("%s\n", buffer);
        }while(!salir);

        close(socket_datos);
    }while(!salir);

    close(socket_control);

    exit(0);
}
