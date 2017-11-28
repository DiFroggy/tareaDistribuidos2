import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.net.SocketException;
import java.net.DatagramSocket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;


public class server{
  public static class ready{
    int ready=0;
  }
  public static class listenerS implements Runnable {
    DatagramPacket msgPacket;
    int flag=0;
    List<Integer> puertos;
    byte[] buf;
    ready ready;
    public listenerS(DatagramPacket mensaje,byte[] buffer,List<Integer> ports,ready r){
      this.msgPacket=mensaje;
      this.buf=buffer;
      this.puertos=ports;
      this.ready=r;
    }

    public void run(){
      InetAddress ipProceso=msgPacket.getAddress();
      int portProceso=msgPacket.getPort();
      String msg=new String(buf, 0, msgPacket.getLength());
      //r: Mensaje es un registro de puerto
      //p: Mensaje es petición del puerto de un proceso.
      String patron="(r|p)-(.*)";
      Pattern lector=Pattern.compile(patron);
      Matcher matcher= lector.matcher(msg);
      matcher.find( );
      if(matcher.group(1).equals("r")){
        System.out.println("Registro!");
        System.out.println(matcher.group(2));
        String patroncito="id:([0-9]+) ,puerto:([0-9]+)";
        Pattern lector2=Pattern.compile(patroncito);
        Matcher matcher2= lector2.matcher(matcher.group(2));
        matcher2.find( );
        int id=Integer.parseInt(matcher2.group(1));
        int port=Integer.parseInt(matcher2.group(2));
        puertos.set(id,port);
        ready.ready++;
      }else if(matcher.group(1).equals("p")){
        patron="([0-9]+)";
        Pattern lector2=Pattern.compile(patron);
        Matcher matcher2= lector.matcher(matcher.group(2));
        matcher2.find( );
        int id=Integer.parseInt(matcher2.group(1));
        String mensaje=Integer.toString(puertos.get(id));
        //Por ultimo se envia el paquete.
        DatagramPacket solicitud= new DatagramPacket(mensaje.getBytes(),mensaje.getBytes().length,ipProceso,portProceso);
        try(DatagramSocket clientSocket=new DatagramSocket(0)){
          clientSocket.send(solicitud);
        }catch (IOException ex) {
          ex.printStackTrace();
        }
      }
    }
  }

  public static void main(String[] args) {
    int n=Integer.parseInt(args[1]);
    List<Integer> puertos= new ArrayList<>(n);
    int PORT=Integer.parseInt(args[0]);
    ready ready=new ready();
    for (int i=0;i<n ;i++ ) {
      puertos.add(0);
    }

    try (DatagramSocket clientSocket = new DatagramSocket(PORT)){
      while (true) {
        byte[] buf = new byte[256];
        DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
        clientSocket.receive(msgPacket);
        Runnable r = new listenerS(msgPacket,buf,puertos,ready);
        new Thread (r).start();
        System.out.println("Mensaje!");
        System.out.println(ready.ready);
        if (ready.ready==n-1) {
            break;
        }
      }
      InetAddress address=InetAddress.getByName("224.0.1.1");
      int portm=8001;
      String men="Listaylor Swift";
      System.out.println(men);
      try (DatagramSocket sSocket = new MulticastSocket(portm)) {
        DatagramPacket msgP = new DatagramPacket(men.getBytes(),
        men.getBytes().length, address, portm);
        sSocket.send(msgP);
      } catch (IOException ee) {
        ee.printStackTrace();
      }
    } catch (IOException ex) {
        ex.printStackTrace();
    }
  }
}
