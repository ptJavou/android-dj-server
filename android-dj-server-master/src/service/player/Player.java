package service.player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dto.YoutubeVideo;
import service.VideoDurationService;


/**
 * Acessa e modifica os arquivos javascripts.
 * Converte links do youtube recebidos da aplicação android para que executem em browser Desktop.
 * 
 * @author ramoncosta
 *@version 1.0
 */
public class Player {
	
	/**
	 * URL padrão do youtube sem o id do vídeo.
	 */
	private final String YOUTUBE_DEFAULT_URL = "https://www.youtube.com/watch?v=";
	public final File JS_YOUTUBE_API  = new File(".\\html\\jsYoutube.html");
	public final File JS_PLAYER_FILE  = new File(".\\html\\player.html");
	private VideoDurationService videoDurationService = VideoDurationService.getInstance();
	
	
	/**
	 * Converte uma youtube url no formato para mobile em url para desktop.
	 * 
	 * @param youtubeAndroidURL youtube url em formato para android
	 * @return youtube url executada em browsers desktop.
	 */
	public String getYoutubeBrowserURL(String youtubeAndroidURL){
		String videoId = getVideoIdFromAndroidYoutubeURL(youtubeAndroidURL);
		String videoLink = YOUTUBE_DEFAULT_URL + videoId;
		System.out.println("youtube id video = " + videoId);
		return videoLink;
	}
	
	/**
	 * Processa a String com o uso de expressões regulares e retorna somente o id do vídeo.
	 * @param link link recebido pela aplicação mobile.
	 * @return o id do vídeo.
	 */
	private String getVideoIdFromAndroidYoutubeURL(String link){
		Pattern pattern1 = Pattern.compile("(?<=youtu.be/)(.+)(?=&t=)");
		Pattern pattern2 = Pattern.compile("(?<=youtu.be/)(.+)");
		
		Matcher m = pattern1.matcher(link);
	
		
		String result = "";
		if(m.find()){
			result = m.group();
			return result;
		}
		
		m = pattern2.matcher(link);
		if(m.find()){
			result = m.group();
		}
		return result;
	}
	
	
	public String getVideoIdFromYoutubeLink(String link){
		Pattern pattern = Pattern.compile("(?<=v=)(.+)");
		
		Matcher m = pattern.matcher(link);
	
		String result = "";
		if(m.find()){
			result = m.group();
		}
		
		return result;
	}
	
	/**
	 * Salva o conteúdo no arquivo
	 * @param jsYoutubeApi arquivo javascript responsavel por executar o vídeo
	 * @param content conteúdo do arquivo
	 * @throws IOException se o arquivo não for encontrado
	 */
	public void saveJsFile(File jsYoutubeApi, String content) throws IOException{
		try(PrintWriter writer = new PrintWriter(new FileWriter(jsYoutubeApi))){
		   writer.println(content);
		   writer.flush();
		}
	}
	
	/**
	 * Lê o arquivo e modifica a linha em que se encontra o id do vídeo.
	 * @param javascriptFile arquivo javascript responsavel por executar o vídeo
	 * @param videoId id do vídeo
	 * @param varLine linha especifica no arquivo javascript que atribui o id do vídeo à variável
	 * @throws IOException se o arquivo não for encontrado
	 */
	public void setYoutubeVideoId(File file, String videoId, int varLine) throws IOException{
		StringBuilder textFile = new StringBuilder();
		try(BufferedReader reader = new BufferedReader(new FileReader(file))){
			
			String line = "";
			int i = 0;
			while((line = reader.readLine()) != null){
				if(i == varLine){
					textFile.append("var videoIdLink = '" + videoId + "'; ").append(System.lineSeparator());
					i++;
					continue;
				}
				
				textFile.append(line).append(System.lineSeparator());
				i++;
			}
		}
		
		saveJsFile(file, textFile.toString());
	}
	
	/**
	 * Lê todas as linhas do arquivo.
	 * 
	 * @param javascriptFile arquivo a ser lido.
	 * @return conteúdo do arquivo.
	 * @throws IOException lança uma exeção se o arquivo não for encontrado.
	 */
	public String getHTMLContent(File javascriptFile) throws IOException{
		String textFile = "";
		
		String line = "";
		try(BufferedReader reader = new BufferedReader(new FileReader(javascriptFile))){
			while((line = reader.readLine()) != null){
				textFile += line;
				textFile += "\n";
			}
		}
		
		return textFile;
	}
	
	/**
	 * modifica a linha em que se encontra a variável que armazena o link do vídeo.
	 * 
	 * @param fileContent
	 * @param videoLink
	 * @param varLine
	 * @return
	 * @throws IOException
	 */
	public String setYoutubeURLInJSVar(String fileContent, String videoLink, int varLine) throws IOException{
		String textFile = null;
		
		try(BufferedReader reader = new BufferedReader(new StringReader(fileContent))){
			
			String line = "";
			textFile = "";
			int i = 0;
			while((line = reader.readLine()) != null){
				if(i == varLine){
					textFile += "var videoLink = '" + videoLink + "'; ";
					textFile += "\n";
					i++;
					continue;
				}
				
				textFile += line;
				textFile += "\n";
				i++;
			}
		}
		
		return textFile;
	}
	
	/**
	 * sobreescreve o arquivo javascript na linha de numero varLine
	 * 
	 * @param text conteúdo do arquivo.
	 * @param videoLenght duração do vídeo em segundos.
	 * @param varLine número da linha a ser modificada.
	 * @return conteúdo do arquivo com as modificações.
	 * @throws IOException se uma I/O exception ocorrer.
	 */
	public String setYoutubeVideoLenghtInJsVar(String text, Long videoLenght, int varLine) throws IOException{
		String textFile = null;
		
		try(BufferedReader reader = new BufferedReader(new StringReader(text))){
			
			String line = "";
			textFile = "";
			int i = 0;
			while((line = reader.readLine()) != null){
				if(i == varLine){
					textFile += "var videoLenght = '" + videoLenght + "'; ";
					textFile += "\n";
					i++;
					continue;
				}
				
				textFile += line;
				textFile += "\n";
				i++;
			}
		}
		
		return textFile;
	}
	
	/**
	 * Tokeniza a requisição GET e retorna a duração do vídeo.
	 * @param requestData  a String recebida via socket.
	 * @return a duração do vídeo enviado pela aplicação em javascript
	 */
	public String getVideoDuration(String requestData){
		Pattern pattern = Pattern.compile("videoDuration=(\\w)+");
		Matcher m = pattern.matcher(requestData);
		
		while(m.find()){
			String keyValue = m.group();
			String[] s = keyValue.split("=");
			
			return s[1];
		}
		
		return null;
	}
	
	/**
	 * Espera pela requisição vinda do aquivo javascript.
	 * Através da jframe youtube api do vídeo em segundos.
	 * Enseguida envia atravé de uma requisição http a duração do vídeo. 
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public Long waitVideoDuration() throws InterruptedException,
	ExecutionException, IOException, URISyntaxException{
			
			while(videoDurationService.getVideoDuration() == null){
				Thread.sleep(500);
			}
			Long secondsVideoDuration = new Long(Long.valueOf(videoDurationService.getVideoDuration()));
			videoDurationService.setVideoDuration(null);
			
			return secondsVideoDuration;		
	}
	
	/**
	 * modifica o valor atribuído a váriável que recebe o id do vídeo no arquivo javascript.
	 * @param ytVideo o vídeo do youtube
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public void setVideoIdInJsFile(YoutubeVideo ytVideo) throws IOException, URISyntaxException{
		String videoId = getVideoIdFromYoutubeLink(ytVideo.getURL());
		setYoutubeVideoId(JS_YOUTUBE_API, videoId, 11);
	}
}
