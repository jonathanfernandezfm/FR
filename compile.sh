#!/bin/bash

ip=`hostname -I`

if [[ $1 = "mate" ]]
then
    gcc -w cliente.c -o cliente
    gcc -w servidor.c -o servidor
    mate-terminal -x ./servidor 9898
    mate-terminal --geometry=60x20+500+200 -x ./cliente $ip 9898
elif [[ $1 = "gnome" ]]
then
    gcc -w cliente.c -o cliente
    gcc -w servidor.c -o servidor
    gnome-terminal -x ./servidor 9898
    gnome-terminal --geometry=60x20+500+200 -x ./cliente $ip 9898
elif [[ $1 = "clean" ]]
then
    rm -f cliente servidor
    echo "Ejecutables eliminados correctamente"
else
    echo "Opcion no permitida. Por favor pase el parametro correcto:"
    echo -e "\tmate\tPara ejecutar en mate-terminal"
    echo -e "\tgnome\tPara ejecutar en gnome terminal"
    echo -e "\tclean\tEliminar ejecutables"
fi
