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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
          //Se limpia 'buf' en cada iteración y luego se crea el objeto que recibirá
          //  el datagrama.
          buf=new byte[256];
          DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
          //Se recibe el paquete.
          clientSocket.receive(msgPacket);
          //Guardar en msg el contenido del datagrama.
          msg = new String(buf, 0, msgPacket.getLength());
					//En caso de que se haya llamado a la interrupción del thread, salir del loop.
					if(msg.equals("kill")){
						break;
					}
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
    int id;
    boolean bearer;
    Semaforo sem;
    List<Integer> rn=new ArrayList<>();
		Token token;
    public Estado(int n,boolean b,int idd,Semaforo semaforo){
      for (int i=0;i<n;i++) {
        rn.add(0);
      }
      this.id=idd;
      this.sem=semaforo;
			this.bearer=b;
      if (bearer == true){
				this.token=new Token(n);
      }
    }
    public int getRN(int id){
      return(rn.get(id));
    }
    public void setRN(int id,int seq){
      rn.set(id,seq);
    }
    public void printRN(){
      log("RN - "+rn,id);
    }
    public void entrarZC() throws RemoteException{
			if(!bearer){
        int seq=rn.get(id)+1;
        rn.set(id,seq);
        sem.request(id,this.getRN(id));
				try {
					log("Semaforo: Amarillo",id);
					token=sem.waitToken(id);
					bearer=true;
				}catch (IOException e) {
					e.printStackTrace();
				}
      }
			log("Semaforo: Rojo",id);
			token.actualizarLN(id,this.getRN(id));
			token.enqueueUnattended(rn);
			log("Semaforo: Verde",id);
			sem.avisarTermino(id);
			while(true){
				if (!sem.getTermino()) {
					token.printearLN();
					int resultado=sem.takeToken(token);
					if(resultado==0){
						break;
					}
					try{
						Thread.sleep(1000);
					}catch (InterruptedException e) {
						e.printStackTrace();
					}
				}else{
					break;
				}
				token.actualizarLN(id,this.getRN(id));
				token.enqueueUnattended(rn);
			}
			token=null;
			bearer=false;
    }
  }
	public static void log(String msg,int id){
		BufferedWriter bw = null;
		FileWriter fw = null;
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		String data="["+dateFormat.format(date)+" - P"+Integer.toString(id)+"] "+msg+"\n";
		try {
			File file = new File("log.txt");
			// if file doesnt exists, then create it
			file.createNewFile();
			// true = append file
			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);
			bw.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
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
		int port=1098;
    int seq=0;
    Token token;
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

    Semaforo sem = (Semaforo) Naming.lookup("rmi://localhost:"+Integer.toString(port)+"/Semaforo");
    Estado estado=new Estado(n,bearer,id,sem);
    System.out.println("ID: "+id+"|| Procesos: "+n+"|| Delay: "+initialDelay+"|| Bearer: "+bearer);
    Runnable r= new listenerP(portm,address,estado);
    Thread nip= new Thread(r);
    nip.start();
		sem.avisarInicio(id);
		while(true){
			if(sem.getInicio()){
				System.out.println("Todos los procesos se han inicializado.");
				break;
			}
			try {
				Thread.sleep(1000);
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		log("Semaforo: Verde",id);
		try {
			Thread.sleep(initialDelay);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		estado.entrarZC();
		while (true) {
			if(sem.getTermino()){
				sem.kill();
				System.out.println("Todos los procesos han acabado de procesar su zona critica.");
				break;
			}
		}
  }
}
