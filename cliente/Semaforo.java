package cliente;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.IOException;
public interface Semaforo extends Remote{
	void request(int id, int seq) throws RemoteException;
	Token waitToken(int id) throws RemoteException,IOException;
	int takeToken (Token token) throws RemoteException;
	void kill() throws RemoteException;
	int avisarInicio(int id) throws RemoteException;
	int avisarTermino(int id) throws RemoteException;
  boolean getInicio() throws RemoteException;
  boolean getTermino() throws RemoteException;
}
