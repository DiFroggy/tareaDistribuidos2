package cliente;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Semaforo extends Remote{
	void request(int id, int seq) throws RemoteException;
	void waitToken() throws RemoteException;
	void takeToken (Token token) throws RemoteException;
	void kill() throws RemoteException;
}
