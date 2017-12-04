/*Process
*
*v1.0: Primera implementación funcional de Process
*
*04/12/2017
*
*Donut steel pls
*/
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

public class Process{
 /**listenerP: Clase que implementa un puerto multicast para recibir request.
  *						 Requests son emitidos por procesos por RMI y luego enviados por
	*            Multicast a cada proceso.*/
	public static class listenerP implements Runnable{
    public int portm;
    public InetAddress address;
    public Estado estado;
    public listenerP(int puerto,InetAddress dir,Estado state){
      this.portm=puerto;
      this.address=dir;
      this.estado=state;
    }
    public void run(){
      try (MulticastSocket clientSocket = new MulticastSocket(portm)){
        String msg;
        byte[] buf=new byte[256];
        clientSocket.joinGroup(address);
        while (true) {
          buf=new byte[256];
          DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
          clientSocket.receive(msgPacket);
          msg = new String(buf, 0, msgPacket.getLength());
					if(msg.equals("kill")){
						break;
					}
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
        clientSocket.leaveGroup(address);
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
	/*Estado: Clase que indica el estado interno del proceso y ofrece métodos durante
	*         la ejecución de procesos.*/
  public static class Estado{
    int id;
    boolean bearer;
    Semaforo sem;
    List<Integer> rn=new ArrayList<>();
		Token token;
		//Constructor de la clase
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
		//getRN: Obtiene el número de secuencia registrado en el proceso del proceso id.
    public int getRN(int id){
      return(rn.get(id));
    }
		//setRN: Define el número de secuenci registrado en el proceso del proceso id.
    public void setRN(int id,int seq){
      rn.set(id,seq);
    }
		//printRN: Método intermedio que llama a log para escribir en el archivo la lista RN.
    public void printRN(){
      log("RN - "+rn,id);
    }
		/*entrarZC: Método que define la lógica de entrada a la zona crítica,
		*					  ejecutando los métodos anteriores para mantener la consistencia
		*						del log y gestionando el movimiento del token.
		*/
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
					bearer=false;
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
	/*log: Método que escribe en un archivo log.txt el mensaje msg, indicando la
				 hora del log e id del proceso.*/
	public static void log(String msg,int id){
		BufferedWriter bw = null;
		FileWriter fw = null;
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		String data="["+dateFormat.format(date)+" - P"+Integer.toString(id)+"] "+msg+"\n";
		try {
			File file = new File("log.txt");
			file.createNewFile();
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

	/*main: Primero se definen las variables, obtiene los métodos rmi a través del
	*				objeto 'sem'. Luego se espera a la inicialización de todos los procesos
	*				utilizando avisarInicio() y getInicio(). Posteriormente se llama a
	*       ejecutarZC() para la ejecución del algoritmo y finalmente se asegura que
	*				todos los procesos hayan terminado usando avisarTermino() y getTermino().
	*/
  public static void main(String[] args) throws UnknownHostException,NotBoundException,MalformedURLException,RemoteException{
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
