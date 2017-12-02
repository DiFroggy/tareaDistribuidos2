package servidor;

import java.util.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.net.MalformedURLException;
import cliente.Semaforo;
import cliente.Token;

public class SemaforoImp extends UnicastRemoteObject implements Semaforo {
  public SemaforoImp() throws RemoteException{
    super();
  }
  public void kill(){
    System.out.println("KILL LA KILL");
    try {

      Thread.sleep(10000);
    }catch (InterruptedException e) {

    }
    System.out.println("hey");
  }
  public void request(int id, int seq){
    System.out.println("req");
  }
	public void waitToken(){
    System.out.println("waitforit");
  }
	public void takeToken (){
    System.out.println("takem");
  }

  public static void main(String[] args) {
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
