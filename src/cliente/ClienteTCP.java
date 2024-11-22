package cliente;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.Arrays;

/**
 * TODO: Complementa esta clase para que genere la conexi�n TCP con el servidor
 * para enviar un boleto, recibir la respuesta y finalizar la sesion
 */
public class ClienteTCP {

	private Socket socketCliente = null;
	private BufferedReader entrada = null;
	private PrintWriter salida = null;

	/**
	 * Constructor
	 */
	
	// Aqui establezco la conexión del clienteTCP
	public ClienteTCP(String ip, int puerto) {
		try {
			// Creo el socket y la entrada y la salida
			socketCliente = new Socket(ip, puerto);
			System.out.println("Conexión establecida: " + socketCliente);
			entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
			salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream())), true);
		} catch (IOException e) {
			System.err.printf("Imposible conectar con ip:%s / puerto:%d", ip, puerto);
			System.exit(-1);
		}
	}

	/**
	 * @param combinacion que se desea enviar
	 * @return respuesta del servidor con la respuesta del boleto
	 */
	public String comprobarBoleto(int[] combinacion) {
		
		// Ordeno el array con la combinacion introducida por el cliente
		Arrays.sort(combinacion);
			
		String combinacionString = "";
		
		// Bucle for en el que paso el array a un string para poder mandarlo al servidor
		for (int i = 0; i < combinacion.length; i++) {
			if (i != combinacion.length - 1) {
				combinacionString = combinacionString + combinacion[i] + " ";
			} else {
				combinacionString = combinacionString + combinacion[i];
			}
			
		}
		
		// Mando el string al servidor
		salida.println(combinacionString);
		
		String respuesta = "";
		
		try {
			// Leo la respuesta final que me da el servidor
			respuesta = entrada.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return respuesta;
	}

	/**
	 * Sirve para finalizar la la conexión de Cliente y Servidor
	 */
	public void finSesion() {
		try {
			// Cierro la salida, la entrada y el socket para finalizar la conexion
			salida.close();
			entrada.close();
			socketCliente.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("-> Cliente Terminado");
	}

}
