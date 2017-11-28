import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
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
	public static class token implements Serializable{
    	List<Integer> ln=new ArrayList<>();
    	Queue<Integer> que = new LinkedList<>();
    	String a="Hola soy el token";
    	public token(int n){
    		for (int i=0;i<n;i++) {
        		ln.add(0);
        		System.out.println(a);
     		}
    	}
    	void cambiarMensaje(){
    		a= "soy el token cambiado";
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
	      if (bearer == true){
	      	System.out.println("ES TRUE");
	      	token toki= new token(n);
					//String tokenString=JSON.stringify(toki);

		    	toki=null;
				}
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
	public static class listenerUnicast implements Runnable{
    //Definicion de variables para almacenar ip y port multicast.
    public semaforo sem;
		InetAddress servC;
		int PORT;
		int id;
    public listenerUnicast(semaforo s,InetAddress serv,int por,int idd){
      this.sem=s;
			this.servC=serv;
			this.PORT=por;
			this.id=idd;
    }
    //Codigo del thread.
    public void run(){
      //Se abre el socket multicast para escuchar al puerto indicado.
			try (DatagramSocket clientSocket = new DatagramSocket(0)){
				String registro="r-id:"+Integer.toString(id)+" ,puerto:"+Integer.toString(clientSocket.getLocalPort());
				System.out.println(registro);
				DatagramPacket solicitud= new DatagramPacket(registro.getBytes(),registro.getBytes().length,servC,PORT);
	      clientSocket.send(solicitud);
				byte[] buf = new byte[256];
				DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
				clientSocket.receive(msgPacket);
				String msg=new String(buf, 0, msgPacket.getLength());
				//token toki=mapper.readValue(msg,token.class);
				//s.getToken(toki);
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
					if(msg.equals("Listaylor Swift")){
						System.out.println(msg);
						continue;
					}
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

	public static void main(String[] args) throws UnknownHostException{
	    //Inicializamos todas las variables
	    int id=Integer.parseInt(args[0]);
	    int n=Integer.parseInt(args[1]);
	    int initialDelay=Integer.parseInt(args[2]);
	    String strbearer=args[3];
	    boolean bearer=false;
			int portm=8001;
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
	    System.out.println("ID: "+id+"|| Procesos: "+n+"|| Delay: "+initialDelay+"|| Bearer: "+bearer);
	    semaforo sem=new semaforo(n,bearer,id);
	    System.out.println(sem.bearer);
      Runnable r= new listenerP(portm,address,sem);
      Thread nip= new Thread(r);
      nip.start();
			Runnable u= new listenerUnicast(sem,servC,PORT,id);
      Thread nup= new Thread(u);
      nup.start();
		}
  }
