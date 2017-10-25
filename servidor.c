// servidor.c

#include <stdio.h>
#include <malloc.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <sys/time.h>

#include <stdlib.h>

int main(int argc, char** argv) {
    int socket_control, from_len, socket_datos;
    int nbytes;
    struct sockaddr_in from, sockname;
    char buffer[82];
    char optionMenu[128] = "**********************\n - NEW USER * PASS *\n - LOGIN * PASS *\n - EXIT\n**********************\n";
    char gameMenu[128] = "**********************\n - GET SCORE\n - NEW WORD\n - EXIT\n**********************\n";
    char playingMenu[128] = "**********************\n - WORD *\n - RESOLVE\n - NEW WORD\n - EXIT\n**********************\n";
    int salir = 0;

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
            perror("Cliente: error en la llamada a la funcion send"),
            exit(1);
        printf("Conexión establecida. Option Menu\n");
        do{
            nbytes = recv(socket_datos, buffer, 80, 0);
            if(nbytes == -1)
                perror("Servidor: error Recv"),
                exit(1);

            if(nbytes == 0)
                perror("El cliente se ha desconectado"),
                exit(1);

            printf("El mensaje recibido fue:\n%s\n", buffer);
        }while(strcmp(buffer, "FIN") != 0);

        close(socket_datos);
    }while(!salir);

    close(socket_control);

    exit(0);
}
