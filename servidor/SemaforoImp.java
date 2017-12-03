package servidor;

import cliente.Semaforo;
import cliente.Token;

import java.util.*;
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
  static InetAddress address;

  public SemaforoImp() throws RemoteException{
    super();
  }
  public void kill(){
    System.out.println("KILL LA KILL");
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
	public Token waitToken(){
    System.out.println("waitforit");
  }

  public void takeToken (Token token){
    try {
      Thread.sleep(5000);
      
    }catch (InterruptedException e) {

    }
  }

  public static void main(String[] args) throws UnknownHostException {
    address=InetAddress.getByName("224.0.1.1");
    int port=1099;
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
