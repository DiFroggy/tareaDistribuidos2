import java.util.*;
class process{
  public static void main(String[] args) {
    int id=Integer.parseInt(args[0]);
    int n=Integer.parseInt(args[1]);
    int initialDelay=Integer.parseInt(args[2]);
    String strbearer=args[3];
    boolean bearer;
    if (strbearer.equals("true")) {
      bearer=true;
    }else if (strbearer.equals("false")) {
      bearer=false;
    }else{
      System.out.println("'Bearer' invalido.");
      System.exit(0);
    }
    List<Integer> rn=new ArrayList<>();
    for (int i=0;i<n;i++) {
      rn.add(0);
    }
    System.out.println(rn);
  }
}
