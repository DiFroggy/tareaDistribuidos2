/*SemaforoImp
*
*v1.0: Primera implementación funcional de SemaforoImp
*
*04/12/2017
*
*Donut steel pls
*/
package servidor;

import cliente.Semaforo;
import cliente.Token;

import java.util.*;
import java.io.*;
import java.rmi.Naming;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.net.MalformedURLException;
import java.io.IOException;
import java.net.UnknownHostException;

import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
public class SemaforoImp extends UnicastRemoteObject implements Semaforo {
  static List<Integer> estado=new ArrayList<>(); // 0 No inicializado, 1 Inicializado , 2 Terminado
  static int portm=8001;
  static InetAddress address;
  static boolean inicio=false;
  static boolean termino=false;
  static int n;
  static boolean hasToken=false;
  static Token token;
  static int killingSpree;

  //Constructor
  public SemaforoImp() throws RemoteException{
    super();
  }

  /*kill: Aumenta el valor de killingSpree, variable utilizada para definir
  *       cuantos procesos han llamado al término del programa.*/
  public void kill(){
    killingSpree++;
  }

  /*request: Método que recibe un request de un proceso y lo difunde utilizando
  *          mensajes multicast a los demás procesos.*/
  public void request(int id, int seq){
    String men="id: "+Integer.toString(id)+", seq: "+Integer.toString(seq);
    try (DatagramSocket sSocket = new MulticastSocket(portm)) {
      DatagramPacket msgP = new DatagramPacket(men.getBytes(),
      men.getBytes().length, address, portm);
      sSocket.send(msgP);
      sSocket.close();
    } catch (IOException ee) {
      ee.printStackTrace();
    }
  }

  /*waitToken: Método que deja en espera al proceso hasta que su id sea el
  *           primero en la cola del token. Posteriormente retorna al objeto
  *           token.  */
	public Token waitToken(int id){
    while(true){
      if(hasToken&&token.size()!=0){
        System.out.println(token.peek());
        if(token.peek()==id){
          break;
        }
      }
      try {
        Thread.sleep(1000);
      }catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    token.pop();
    hasToken=false;
    return(token);
  }

  /*takeToken: Método que entrega el token desde el proceso mientras tenga
  *           requests encolados para evitar que quede atrapado en el servidor.
  */
  public int takeToken (Token t){
    token=t;
    if(token.size()==0){
      return(-1);
    }
    hasToken=true;
    return(0);
  }

  /*avisarInicio: Método que indica la inicialización del proceso 'id'. Cada
  *               vez que es llamado, ve si todos los procesos en la lista
  *               'estado' han sido inicializados para cambiar la variable
  *               'inicio'.*/
  public int avisarInicio(int id){
    estado.set(id,1);
    for (int i =0;i<n ;i++ ) {
      if (estado.get(i)==0) {
        return(0);
      }
    }
    inicio=true;
    return(1);
  }

  /*avisarTermino: Método que indica el termino del proceso 'id'. Cada vez
  *               que es llamado, chequea si todos los procesos en la lista
  *               estado han finalizado su ejecución para cambiar la variable
  *              'termino'.*/
  public int avisarTermino(int id){
    estado.set(id,2);
    for (int i=0 ;i<n ;i++ ) {
      if (estado.get(i)==1) {
        return(0);
      }
    }
    termino=true;
    return(1);
  }

  /*getInicio: Retorna 'estado', que indica que todos los procesos se han
  *            inicializado.
  */
  public boolean getInicio(){
    return inicio;
  }

  /*getTermino: Retorna 'termino', que indica que todos los procesos se han
  *            terminado de ejecutar.
  */
  public boolean getTermino(){
    return termino;
  }

  /*main: La creme de la creme, primero registra los métodos rmi y
  *       posteriormente espera a que todos los procesos hayan finalizado
  *       para así cerrar los threads multicast de los procesos y terminar
  *       el algoritmo.
  */
  public static void main(String[] args) throws UnknownHostException {
    address=InetAddress.getByName("224.0.1.1");
    int port=1098;
    n=Integer.parseInt(args[0]);
    killingSpree=0;
    for (int i=0;i<n;i++ ) {
        estado.add(0);
    }
    String url = "rmi://localhost:"+Integer.toString(port)+"/Semaforo";
    try {
      LocateRegistry.createRegistry(port);
      SemaforoImp obj=new SemaforoImp();
      Naming.rebind(url,obj);
      while(true){
        if (killingSpree==n) {
          String men="kill";
          System.out.println(men);
          try (DatagramSocket sSocket = new MulticastSocket(portm)) {
            DatagramPacket msgP = new DatagramPacket(men.getBytes(),
            men.getBytes().length, address, portm);
            sSocket.send(msgP);
            sSocket.close();
          } catch (IOException ee) {
            ee.printStackTrace();
          }
          System.out.println("Cerrando servidor.");
          System.exit(0);
        }else{
          try {
            Thread.sleep(1000);
          }catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    } catch (RemoteException | MalformedURLException e){
      e.printStackTrace();
    }
  }
}
