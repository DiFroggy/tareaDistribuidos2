import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

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
	      	System.out.println("blablabla ZONA TIZA blabla");
	      	toki.cambiarMensaje();
	      	System.out.println(toki.a);
	      	try {
		         FileOutputStream fileOut = new FileOutputStream("token.txt");
		         ObjectOutputStream out = new ObjectOutputStream(fileOut);
		         out.writeObject(toki);
		         out.close();
		         fileOut.close();
		      } catch (IOException i) {
		         i.printStackTrace();
		      }
		    toki=null;

		    try {
		         FileInputStream fileIn = new FileInputStream("token.txt");
		         ObjectInputStream in = new ObjectInputStream(fileIn);
		         toki = (token) in.readObject();
		         in.close();
		         fileIn.close();
		      } catch (IOException i) {
		         i.printStackTrace();
		         return;
		      } catch (ClassNotFoundException c) {
		         System.out.println("token class not found");
		         c.printStackTrace();
		         return;
		      }
		    System.out.println(toki.a);

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
	    System.out.println("ID: "+id+"|| Procesos: "+n+"|| Delllllay: "+initialDelay+"|| Bearer: "+bearer);
	    semaforo sem=new semaforo(n,bearer,id);
	    System.out.println(sem.bearer);
	}
  }
