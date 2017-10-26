#!/bin/bash

gcc cliente.c  -o cliente
gcc servidor.c -w -o servidor

mate-terminal -x ./servidor 9898
mate-terminal --geometry=60x20+500+200 -x ./cliente 192.168.229.128 9898
