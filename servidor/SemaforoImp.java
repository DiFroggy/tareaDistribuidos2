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
  static int portm=8001;
  static List<Integer> puertos=new ArrayList<>();
  static List<Integer> estado=new ArrayList<>(); // 0 No inicializado, 1 Inicializado , 2 Terminado
  static InetAddress address;
  static boolean inicio=false;
  static boolean termino=false;
  static int n;
  static boolean hasToken=false;
  static Token token;
  //TODO hacer comprobacion de que todos los procesos fueron inicializados
  public SemaforoImp() throws RemoteException{
    super();

  }
  public void kill(){
    System.out.println("Dude with a gun: Die potato!");
    System.out.println("Potato: Gracias Boina.");
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
  }
  public void request(int id, int seq){
    String men="id: "+Integer.toString(id)+", seq: "+Integer.toString(seq);
    System.out.println(men);
    try (DatagramSocket sSocket = new MulticastSocket(portm)) {
      DatagramPacket msgP = new DatagramPacket(men.getBytes(),
      men.getBytes().length, address, portm);
      sSocket.send(msgP);
      sSocket.close();
    } catch (IOException ee) {
      ee.printStackTrace();
    }
  }
	public Token waitToken(int id){
    /*
    try(DatagramSocket clientSocket = new DatagramSocket(0)){
      byte[] buf = new byte[256];
      registrarPuerto(id,clientSocket.getLocalPort());
      DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
      clientSocket.receive(msgPacket);
      System.out.println("Recibi algo!");
      try {
        ByteArrayInputStream byteArray = new ByteArrayInputStream( buf );
        ObjectInputStream objInput = new ObjectInputStream( byteArray );
        token = (Token) objInput.readObject();
        objInput.close();

      }catch (ClassNotFoundException e) {
        e.printStackTrace();
      }

    }catch (IOException e) {
      e.printStackTrace();
    }*/
    /*
    if(id==token.peek()){
      hasToken=false;
      return(token);
    }*/
    System.out.println("ID del que espera: "+Integer.toString(id));
    while(true){
      if(hasToken&&token.size()!=0){
        System.out.println("ID del siguiente en la cola"+Integer.toString(token.peek()));
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
    System.out.print("Pop: ");
    System.out.println(token.pop());
    hasToken=false;
    return(token);
  }

  public int takeToken (Token t){
    /*
    try (DatagramSocket clientSocket = new DatagramSocket(0)){
      if(token.size()==0){
        System.out.println("No hay nada en el queue!");
        clientSocket.close();
        return(-1);
      }
      int id=token.pop();
      System.out.println(id);
      ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
      ObjectOutputStream objOutput = new ObjectOutputStream(byteArray);
      objOutput.writeObject(token);
      objOutput.close();
      byte[] buf = byteArray.toByteArray();
      try {
        Thread.sleep(10000);
      }catch (InterruptedException e) {
        e.printStackTrace();
      }
      DatagramPacket msgPacket = new DatagramPacket(buf, buf.length,address,puertos.get(id));
      clientSocket.send(msgPacket);

    }catch (IOException ee) {
      ee.printStackTrace();
    }*/
    token=t;
    if(token.size()==0){
      return(-1);
    }
    System.out.println(token.peek());
    hasToken=true;
    return(0);

  }
  public void registrarPuerto(int id,int puerto){
    puertos.set(id,puerto);
    System.out.println(puerto);
  }
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
  public boolean getInicio(){
    return inicio;
  }
  public boolean getTermino(){
    return termino;
  }

  public static void main(String[] args) throws UnknownHostException {
    address=InetAddress.getByName("224.0.1.1");
    int port=1099;
    n=Integer.parseInt(args[0]);
    for (int i=0;i<n;i++ ) {
        puertos.add(0);
        estado.add(0);
    }
    String url = "rmi://localhost:1099/Semaforo";
    try {
      LocateRegistry.createRegistry(port);
      SemaforoImp obj=new SemaforoImp();
      Naming.rebind(url,obj);
      //Semaforo stub = (Semaforo) UnicastRemoteObject.exportObject(obj,0);

    } catch (RemoteException | MalformedURLException e){
      e.printStackTrace();
    }
  }
}
