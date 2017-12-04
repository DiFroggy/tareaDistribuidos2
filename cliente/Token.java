package cliente;

import java.io.Serializable;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.*;

public class Token implements Serializable{
  Vector<Integer> ln = new Vector<Integer>();
  Queue<Integer> tokenq = new LinkedList<Integer>();
  public Token(int n){
    for (int i=0;i<n;i++) {
      ln.add(0);
    }
  }
  public void actualizarLN(int id,int seq){
    ln.set(id,seq);
  }
  public void enqueueUnattended(List<Integer> rn){
    for (int i=0;i<rn.size() ;i++ ) {
      if (rn.get(i)==ln.get(i)+1) {
        tokenq.add(i);
      }
    }
  }
  public static void log(String msg){
		BufferedWriter bw = null;
		FileWriter fw = null;
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
    String data="["+dateFormat.format(date)+" - Token] "+msg+"\n";
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
  public int pop(){
    int id=tokenq.element();
    tokenq.remove();
    return(id);
  }
  public int size(){
    return(this.tokenq.size());
  }
  public int peek(){
    return(tokenq.peek());
  }
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
    //TODO mandar token tras hace pop
  }
  }
