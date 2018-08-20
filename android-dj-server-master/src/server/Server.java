package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * @author ramoncosta
 * @version 1.0
 * 
 * Servidor Multithread - implementa��o simples de um servidor multithread.
 * Cria uma thread para cada cliente que conectar.
 *
 */
public class Server {

	/**
	 * Porta do sistema operacional utilizada pelo servidor.
	 */
	private final int PORT = 3330; 
	
	/**
	 * Cria uma inst�ncia de ServerSocket utilizando a vari�vel de inst�ncia PORT
	 * @return Retorna uma inst�ncia de java.net.ServerSocket
	 * @throws IOException 
	 */
	private ServerSocket getServerSocket() throws IOException{
		ServerSocket server = new ServerSocket(PORT);
		
		return server;
	}
	
	/**
	 * Inicia o Servidor.
	 * Mant�m o servidor em loop at� o fim do processo.
	 * Para cada cliente conectado um objeto SocketClientThread � criado para tratamento das requisi��es.
	 */
	private void startup(){
		
		System.out.println("Server startup");
		
		try(ServerSocket serverSocket = getServerSocket()){				
			
			while(true){
				
				Socket socket = serverSocket.accept();
				SocketClientThread.getNewSocketThread(socket)
				.start();
				
			}
			
		}catch(IOException | NullPointerException e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Inicia o servidor em uma nova Thread
	 */
	public void start(){
		startup();
	}
}







