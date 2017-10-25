// cliente.c

#include <stdio.h>
#include <malloc.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <sys/time.h>

#include <stdlib.h>
#include <stdio.h>

int main(int argc, char** argv) {
    int socket_datos;
    struct sockaddr_in sockname;
    char buffer[82];

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
