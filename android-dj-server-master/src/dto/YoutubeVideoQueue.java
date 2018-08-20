package dto;


import java.util.LinkedList;
import java.util.Queue;

/**
 * Classe padr�o singleton com a fila de links e m�todos para gerenciar a fila.
 * @author ramoncosta
 * @version 1.0
 *
 */
public class YoutubeVideoQueue {

	/**
	 * Fila para salvar objetos do tipo YoutubeLinkDTO
	 */
	private Queue<YoutubeVideo> videoQueue = new LinkedList<YoutubeVideo>();

	private static YoutubeVideoQueue instance = null;
	private YoutubeVideoQueue(){}
	
	/**
	 * Bloco sincronizado por est� em um contexto multithread.
	 * @return inst�ncia da pr�pia classe
	 */
	public static YoutubeVideoQueue getInstance() {
		if(instance == null){
			synchronized (YoutubeVideoQueue.class) {
				if(instance == null){
					instance = new YoutubeVideoQueue();
				}
			}
		}
		return instance;
	}
	
	/**
	 * Adiciona o objeto par�metro a fila.
	 * M�todo sincronizado por estar em um contexto multithread
	 * @param y a ser adicionado a fila
	 */
	public synchronized void addYoutubeLink(YoutubeVideo y){
		videoQueue.add(y);
	}
	
	public void interateList(){
		videoQueue.forEach(System.out::println);
	}
	
	public YoutubeVideo getHead(){
		return videoQueue.peek();
	}
	
	public void removeQueueHead(){
		videoQueue.remove();
	}
	
	public boolean queueIsEmpty(){
		return videoQueue.isEmpty();
	}

	public Queue<YoutubeVideo> getVideoQueue() {
		return videoQueue;
	}

	public Integer getSize(){
		return videoQueue.size();
	}
	
	
}
