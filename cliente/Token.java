/*Token
*
*v1.0: Primera implementación funcional de Token
*
*04/12/2017
*
*Donut steel pls
*/

package cliente;

import java.io.Serializable;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.*;

public class Token implements Serializable{
  Vector<Integer> ln = new Vector<Integer>();
  Queue<Integer> tokenq = new LinkedList<Integer>();

  //Constructor
  public Token(int n){
    for (int i=0;i<n;i++) {
      ln.add(0);
    }
  }

  //actualizarLN: Actualiza el nro de secuencia del proceso ID en la lista ln.
  public void actualizarLN(int id,int seq){
    ln.set(id,seq);
  }

  /*enqueueUnattended: Encola todos los procesos que tengan un request sin atender
  *                    y que no se encuentre previamente encolados.*/
  public void enqueueUnattended(List<Integer> rn){
    for (int i=0;i<rn.size() ;i++ ) {
      if (rn.get(i)==ln.get(i)+1&&!tokenq.contains(i)) {
        tokenq.add(i);
      }
    }
  }

  //log: Escribe en log.txt el estado actual del token.
  public static void log(String msg){
		BufferedWriter bw = null;
		FileWriter fw = null;
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
    String data="["+dateFormat.format(date)+" - Token] "+msg+"\n";
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

  //pop: Desencola el primer elemento de la cola y lo retorna.
  public int pop(){
    int id=tokenq.element();
    tokenq.remove();
    return(id);
  }

  //size: Devuelve el tamaño de la cola.
  public int size(){
    return(this.tokenq.size());
  }

  //peek: Retorna el primer elemento de la cola sin desencolarlo.
  public int peek(){
    return(tokenq.peek());
  }

  //printearLN: Da formato al string para ser enviado a log().
  public void printearLN(){
    String msg="LN - [";
    for (int i=0;i<ln.size() ;i++ ) {
      msg=msg+Integer.toString(ln.get(i));
      if (i==ln.size()-1) {
        break;
      }
      msg=msg+",";
    }
    msg=msg+"]";
    List<Integer> listaT=new ArrayList<>();
    while(tokenq.size()!=0){
      listaT.add(tokenq.element());
      tokenq.remove();
    }
    for (int i =0;i<listaT.size() ;i++ ) {
      tokenq.add(listaT.get(i));
    }
    log(msg+" Queue - "+listaT);
  }
}
