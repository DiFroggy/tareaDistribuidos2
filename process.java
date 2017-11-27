import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;
import java.io.Serializable;


public class process{
  public static class token implements Serializable{
    List<Integer> ln=new ArrayList<Integer>();
    Queue<Integer> que = new LinkedList<Integer>();
    public token(int n){
      for (int i=0;i<n;i++) {
        ln.add(0);
      }
    }
    void encolar(int id){
      que.add(id);
    }
    void desencolar(){
      que.remove();
    }
    void actualizarLista(int id, int seq){
      ln.set(id,seq);
    }
  }

  public static class semaforo {
    int estado; //TODO 0 indicara verde, 1 indicara amarillo y 2 rojo
    int id;
    boolean bearer;
    List<Integer> rn=new ArrayList<>();
    token toki;
    public semaforo(int n,boolean b,int idd){
      for (int i=0;i<n;i++) {
        rn.add(0);
      }
      this.id=idd;
      this.bearer=b;
    }
    public void request(int id, int seq){
      if(rn.get(id)<seq){
        rn.set(id,seq);
      }
      if(estado==0&&bearer){
        if(rn.get(id)==toki.ln.get(id)+1){
          //TODO mandar token al proceso ID
        }
      }
    }
    public void waitToken(){

    }

    public void takeToken(token tok){
      this.toki=tok;
      bearer=true;
    }
    public void passToken(int id){

    }
    public void kill(){

    }

  }
  public static class floodPuertos implements Runnable {
    //Definicion de variables para almacenar ip y port multicast.
    public int portm;
    public InetAddress address;
    List<Integer> puertos;
    List<boolean> ready;
    public int n;
    public boolean listo=false;
    public DatagramSocket tokenSocket;
    public int termino;
    public floodPuertos(int puerto,InetAddress dir,int nro,List<boolean> lista,DatagramSocket socket,int listongo){
      this.n=nro;
      this.ready=lista;
      this.tokenSocket=socket;
      this.termino=listongo;
    }
    //Codigo del thread.
    public void run(){
      //MANDAR MENSAJE CON EL PUERTO DEFINIDO EN MAIN
      while (!listo) {
        for (int i=0;i<n ;i++ ) {
          if (ready.get(i)==false) {
            break;
          }
          listo=true;
        }

      }
      int localport=tokenSocket.getLocalPort();
      String men="id: "+Integer.toString(id)+",puerto: "+Integer.toString(localport)+"listo: "+Integer.toString(termino);
      try (DatagramSocket sSocket = new MulticastSocket(portm+1)) {
        DatagramPacket msgP = new DatagramPacket(men.getBytes(),men.getBytes().length, address, portm+1);
        while (true){
          System.out.println(nup.getState());
          System.out.println(nup.getState().getClass());
          /*if(estado.equals("TERMINATED")){
            break;
          }*/
          sSocket.send(msgP);
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
  public static class multicastPuertos implements Runnable{
    //Definicion de variables para almacenar ip y port multicast.
    public int portm;
    public InetAddress address;
    List<Integer> puertos;
    List<boolean> ready;
    public int n;
    DatagramSocket socket;
    public int termino=0;
    public multicastPuertos(int puerto,InetAddress dir,int nro,List<Integer> lista,DatagramSocket tokenSocket){
      this.portm=puerto;
      this.address=dir;
      this.puertos=lista;
      this.n=nro;
      this.socket=tokenSocket;
      for (int i=0;i<n ;i++ ) {
        ready.add(false);
      }
    }
    //Codigo del thread.
    public void run(){
      //Se abre el socket multicast para escuchar al puerto indicado.
      try (MulticastSocket clientSocket = new MulticastSocket(this.portm)){
        //buf: Arreglo de bytes utilizado para recibir los paquetes.
        //msg: String donde se maneja el contenido de los paquetes.
        String msg;
        //Se ingresa al grupo multicast.
        clientSocket.joinGroup(this.address);
        Runnable r= new floodPuertos(portm,address,ready,socket);
        Thread nip= new Thread(r);
        nip.Start();

        for (int i=0;i<n-1 ;i++ ) {
          byte[] buf=new byte[256];
          DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
          clientSocket.receive(msgPacket);
          //Guardar en msg el contenido del datagrama.
          msg = new String(buf, 0, msgPacket.getLength());
          //Se utilizan expresiones regulares para extraer la informacion del distrito.
          String patron="id: ([0-9]+), puerto: ([0-9]+), listo: ([0,1])";
          Pattern lector=Pattern.compile(patron);
          Matcher matcher= lector.matcher(msg);
          if (matcher.find( )) {
            puertos.set(Integer.parseInt(matcher.group(1)),Integer.parseInt(matcher.group(2)));
            if(Integer.parseInt(matcher.group(3))==1){
              ready.set(Integer.parseInt(matcher.group(1)),true);
            }
          }
        }
        termino=1;
        nip.Join();
        System.out.println(puertos);
        clientSocket.leaveGroup(address);
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  public static class listenerP implements Runnable{
    //Definicion de variables para almacenar ip y port multicast.
    public int portm;
    public semaforo sem;
    public InetAddress address;
    public listenerP(int puerto,InetAddress dir,semaforo s){
      this.portm=puerto;
      this.address=dir;
      this.sem=s;
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
          //Se limpia 'buf' en cada iteración y luego se crea el objeto que recibirá
          //  el datagrama.
          buf=new byte[256];
          DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
          //Se recibe el paquete.
          clientSocket.receive(msgPacket);
          //En caso de que se haya llamado a la interrupción del thread, salir del loop.
          if(Thread.interrupted()){
            break;
          }
          //Guardar en msg el contenido del datagrama.
          msg = new String(buf, 0, msgPacket.getLength());
          //Se utilizan expresiones regulares para extraer la informacion del distrito.
          String patron="id: ([0-9]+), secuencia: ([0-9]+)";
          Pattern lector=Pattern.compile(patron);
          Matcher matcher= lector.matcher(msg);
          matcher.find( );
          int id=Integer.parseInt(matcher.group(1));
          int seq=Integer.parseInt(matcher.group(2));
          sem.request(id,seq);

        }
        //Si se sale del loop, dejar el grupo multicast.
        clientSocket.leaveGroup(address);
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
  public static void main(String[] args) {
    //Inicializamos todas las variables
    int id=Integer.parseInt(args[0]);
    int n=Integer.parseInt(args[1]);
    int initialDelay=Integer.parseInt(args[2]);
    String strbearer=args[3];
    boolean bearer=false;
    List<Integer> puertos=new ArrayList<>();
    if (strbearer.equals("true")) {
      bearer=true;
    }else if (strbearer.equals("false")) {
      bearer=false;
    }else{
      System.out.println("'Bearer' invalido.");
      System.exit(0);
    }
    semaforo sem=new semaforo(n,bearer,id);
    //Inicializar lista con puertos.
    for (int i=0;i<n ;i++ ) {
      puertos.add(0);
    }
    //Indicamos direccion y puerto multicast
    int portm=8001;
    try{

      InetAddress address=InetAddress.getByName("224.0.0.1");
      //Se inicia thread para escuchar al grupo multicast
      Runnable multP=new multicastPuertos(portm+1,address,n,puertos);
      Thread nup=new Thread(multP);
      nup.start();
      try{
        tokenSocket=new DatagramSocket(0);
        Runnable r= new listenerP(portm,address,sem,tokenSocket);
        Thread nip= new Thread(r);
        nip.start();

      }catch (IOException ex) {
        ex.printStackTrace();
      }
    }catch (UnknownHostException e) {
      e.printStackTrace();
    }
    //Thread.sleep(initialDelay);
    if(!bearer){
      //TODO MANDAR MENSAJE MULTICAST PIDIENDO TOKEN
      //TODO ESPERAR RESPUESTA

    }else {
      sem.takeToken(new token(n));
    }
  }
}
