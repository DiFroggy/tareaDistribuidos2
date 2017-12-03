package cliente;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.regex.Matcher;
import java.rmi.Naming;
import java.util.regex.Pattern;
import java.util.*;

/*TODO Codigo que aun no se DONDE poner.

//Objeto a JSON
ObjectMapper mapper = new ObjectMapper();
String jsonInString=mapper.writeValueAsString(toki);
//JSON a objeto
token toki=mapper.readValue(jsonInString,token.class);
*/

public class process{


	public static class listenerP implements Runnable{
    //Definicion de variables para almacenar ip y port multicast.
    public int portm;
    public InetAddress address;
    public Estado estado;
    public listenerP(int puerto,InetAddress dir,Estado state){
      this.portm=puerto;
      this.address=dir;
      this.estado=state;
    }
    //Codigo del thread.
    public void run(){
      //Se abre el socket multicast para escuchar al puerto indicado.
      try (MulticastSocket clientSocket = new MulticastSocket(portm)){
        //buf: Arreglo de bytes utilizado para recibir los paquetes.
        //msg: String donde se maneja el contenido de los paquetes.
        String msg;
        byte[] buf=new byte[256];
        //Se ingresa al grupo multicast.
        clientSocket.joinGroup(address);

        while (true) {
					System.out.println("Escuchando multicast");
          //Se limpia 'buf' en cada iteración y luego se crea el objeto que recibirá
          //  el datagrama.
          buf=new byte[256];
          DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
          //Se recibe el paquete.
          clientSocket.receive(msgPacket);
					System.out.println("Help");
          //En caso de que se haya llamado a la interrupción del thread, salir del loop.
          if(Thread.interrupted()){
            break;
          }
          //Guardar en msg el contenido del datagrama.
          msg = new String(buf, 0, msgPacket.getLength());
					System.out.println(msg);

          //Se utilizan expresiones regulares para extraer la informacion del distrito.
          String patron="id: ([0-9]+), seq: ([0-9]+)";
          Pattern lector=Pattern.compile(patron);
          Matcher matcher= lector.matcher(msg);
          matcher.find( );
          int id=Integer.parseInt(matcher.group(1));
          int seq=Integer.parseInt(matcher.group(2));
          if (seq>estado.getRN(id)) {
            estado.setRN(id,seq);
          }
          estado.printRN();

        }
        //Si se sale del loop, dejar el grupo multicast.
        clientSocket.leaveGroup(address);
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
  public static class Estado{
    int estado=0; //TODO 0 indicara verde, 1 indicara amarillo y 2 rojo
    int id;
    boolean bearer;
    Semaforo sem;
    List<Integer> rn=new ArrayList<>();
    public Estado(int n,boolean b,int idd,Semaforo semaforo){
      for (int i=0;i<n;i++) {
        rn.add(0);
      }
      this.id=idd;
      this.sem=semaforo;
      if (bearer == true){
        System.out.println("ES TRUE");
        //String tokenString=JSON.stringify(toki);

      }
    }
    public int getRN(int id){
      return(rn.get(id));
    }
    public void setRN(int id,int seq){
      rn.set(id,seq);
    }
    public void printRN(){
      System.out.println(rn);
    }
    public void entrarCS() throws RemoteException{
      if(!bearer){
        int seq=rn.get(id)+1;
        rn.set(id,seq);
        sem.request(id,seq);
        estado=1;
        sem.waitToken();
      }
    }
  }

  public static void main(String[] args) throws UnknownHostException,NotBoundException,MalformedURLException,RemoteException{
    //Inicializamos todas las variables
    int id=Integer.parseInt(args[0]);
    int n=Integer.parseInt(args[1]);
    int initialDelay=Integer.parseInt(args[2]);
    String strbearer=args[3];
    boolean bearer=false;
    int portm=8001;
    int seq=0;
    Token token;

    InetAddress servC=InetAddress.getByName("127.0.0.1");
    int PORT=8888;
    InetAddress address=InetAddress.getByName("224.0.1.1");
    List<Integer> puertos=new ArrayList<>();
    if (strbearer.equals("true")) {
      bearer=true;
    }else if (strbearer.equals("false")) {
      bearer=false;
    }else{
      System.out.println("'Bearer' invalido.");
      System.exit(0);
    }
    if (bearer) {
      token=new Token(n);
    }
    Semaforo sem = (Semaforo) Naming.lookup("rmi://localhost:1099/Semaforo");
    Estado estado=new Estado(n,bearer,id,sem);
    System.out.println("ID: "+id+"|| Procesos: "+n+"|| Delay: "+initialDelay+"|| Bearer: "+bearer);
    Runnable r= new listenerP(portm,address,estado);
    Thread nip= new Thread(r);
    nip.start();
    //sem.request(id,++seq);

  }
}
