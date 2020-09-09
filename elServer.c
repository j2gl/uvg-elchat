/*

  elServer 1.0
  Juan Jose Garcia Lau
  Jose Esteban Maldonado
  Universidad del Valle de Guatemala
  Proyecto de Redes
  Prof. Luis Barrera
  Guatemala, Junio 2000

*/
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <sys/wait.h>
#include <arpa/inet.h>
#include <fcntl.h>

#define MAX_CLIENTES 10
#define BACKLOG 10
#define SERVER_PORT 8196                              	

int client_fd[MAX_CLIENTES],
    nu_cliente = 0;

char nombres[MAX_CLIENTES][256];

int socket_open(int port)
{
  struct sockaddr_in server_addr;
  int sock_fd;
  if ( (sock_fd = socket(AF_INET, SOCK_STREAM, 0)) == -1)  //crea el socket
  {
    perror("socket");
    return -1;
  }
  server_addr.sin_family = AF_INET;          //tipo de conexion de internet host byte order
  server_addr.sin_port = htons(port);        //numero de puerto short network byte order
  server_addr.sin_addr.s_addr = htonl(INADDR_ANY);
  bzero(&(server_addr.sin_zero), 8);

  //asocia el socket con un endpoint, y esta lista para escuchar
  if (bind(sock_fd, (struct sockaddr *)&server_addr, sizeof(struct sockaddr)) == -1) {
    perror("bind");
    return -1;	
  }
  return (sock_fd);
}

int socket_listen(int sock_fd)
{
  if ( listen(sock_fd, BACKLOG) == -1)
  {
    perror("listen");
    return -1;
  }
}

void socket_acepta(int sock_fd)
{
  struct sockaddr_in client_addr;
  //serverSockAddrPtr = (struct sockaddr*) &client_addr;
  //struct timeval tv;
  char
    rec_buff[1024],
    send_buff[1024];



  int sin_size,
      new_fd,
      i,j,l;

  //fd_set readfds;
  //tv.tv_sec = 2;
  //tv.tv_usec = 500000;
  //FD_ZERO(&readfds);

  nu_cliente = 0;
  sin_size = sizeof(struct sockaddr_in);
  while(1)
  {
    for (i = 0; i < MAX_CLIENTES; i++)
    {
      if (client_fd[i] == -1)
      {
        fcntl(sock_fd, F_SETFL, O_NONBLOCK);
        if ((new_fd = accept(sock_fd, (struct sockaddr *)&client_addr, &sin_size)) != -1)
        {
          nu_cliente++;
          client_fd[i] = new_fd;
          fcntl(client_fd[i], F_SETFL, O_NONBLOCK);

          socket_send(client_fd[i], "\n");
          socket_send(client_fd[i], "Bienvenido a elChat");
          sprintf(send_buff, "Hay %d usuarios\n", nu_cliente);
          socket_send(client_fd[i], send_buff);
          bzero(rec_buff,sizeof (rec_buff));

          while ( socket_receive(client_fd[i], rec_buff, sizeof(rec_buff)) == 0);

          nombres[i][strlen(nombres[i])-1]='\0';
          strcpy(nombres[i],rec_buff);
          printf("elServer: %s entro a elChat de la computadora %s\n",
                  nombres[i],inet_ntoa(client_addr.sin_addr));
          printf("elServer: Hay %d usuarios\n",nu_cliente);	
          sprintf( send_buff, "elServer: Entro %s a elChat", nombres[i] );
	  for (j = 0; j < MAX_CLIENTES; j++)
	    if ( (client_fd[j]!=-1) && (j!=i) )
	    {
	
	      socket_send(client_fd[j], send_buff);
	    } //if
        }
      }

      l = socket_receive(client_fd[i], rec_buff, sizeof(rec_buff));
      if ( l>0 )
      {
        if (rec_buff[0] == '#')
        {
          if ( strcmp(rec_buff,"#quit") == 0 )
          {
            close(client_fd[i]);
            client_fd[i] = -1;
            nu_cliente--;
            printf("elServer: %s se salio de elChat\n", nombres[i]);
            printf("elServer: Hay %d usuarios\n",nu_cliente);	
            sprintf( send_buff, " %s se salio de elChat", nombres[i]);
            for (j = 0; j < MAX_CLIENTES; j++)
    	    {
    	      if (client_fd[j]!=-1)
	      { 	
	        socket_send(client_fd[j], send_buff);
   	      } //if
	    } //for
          }
        } //if
        else
        {
          printf("Usuario %d,%s> %s\n", i, nombres[i], rec_buff);
          for (j = 0; j < MAX_CLIENTES; j++)
          {
	    if ( (client_fd[j]!=-1) && (j!=i) )
	    {
	      sprintf( send_buff, "%s> %s", nombres[i],rec_buff);
	      socket_send(client_fd[j], send_buff);
   	    } //if
	  } //for
        } //else
      } //if
    } //for
  } //while
}


int socket_receive(int fd, char *buf, int maxsize /* maxsize >= 1 */ )
{
  char c = '\0';
  int i = 0;
  if ( (read(fd, &c, 1) == 1 ) )
  {
    buf[i] = c;
    i++;
    while ((c != '\n') && (i < maxsize - 1))
    {
      if (read(fd, &c, 1) == 1);
      {  buf[i] = c;
         i++;
      }

    }
    buf[i-1] = '\0';
    i++;
  }
  return i;
}

int socket_send (int fd, const char *buf)
{
   int i;
   i = write(fd, buf, strlen(buf));
   write(fd, "\n", 1);
   return (i >= 0) ? (i + 1) : i;
}

void Inicializar()
{
  int i;
  for (i=0;i<MAX_CLIENTES;i++)
    client_fd[i] = -1;
}

int main()
{
  int fd;
  Inicializar();
  printf("\nelServer 1.0\n\n");
  printf("Juan Jose Garcia Lau\n");
  printf("Jose Esteban Maldonado\n\n");
  printf("Universidad del Valle de Guatemala\n");
  printf("Proyecto de Redes\n");
  printf("Prof. Luis Barrera\n\n");
  printf("Guatemala, Junio 2000\n\n");
  if (( fd = socket_open(SERVER_PORT)) == -1 )
    exit(0);

  if ( socket_listen(fd) == -1)
    exit(0);

  socket_acepta(fd);

  return 0;
}