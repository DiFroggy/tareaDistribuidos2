package cliente;

import java.io.Serializable;
import java.util.*;

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
  public int pop(){
    int id=tokenq.element();
    tokenq.remove();
    return(id);
  }
  //TODO mandar token tras hace pop
}
