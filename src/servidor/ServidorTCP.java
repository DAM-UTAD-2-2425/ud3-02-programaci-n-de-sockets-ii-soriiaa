package servidor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * TODO: Complementa esta clase para que acepte conexiones TCP con clientes para
 * recibir un boleto, generar la respuesta y finalizar la sesion
 */
public class ServidorTCP {
	private String[] respuesta;
	private int[] combinacion;
	private int reintegro;
	private int complementario;
	private Socket socketCliente;
	private ServerSocket socketServidor;
	private BufferedReader entrada;
	private PrintWriter salida;

	/**
	 * Constructor
	 */
	public ServidorTCP(int puerto) {

		this.socketCliente = null;
		this.socketServidor = null;
		this.entrada = null;
		this.salida = null;
		try {
			socketServidor = new ServerSocket(puerto);
			System.out.println("Esperando conexión...");
			socketCliente = socketServidor.accept();
			System.out.println("Conexión acceptada: " + socketCliente);
			entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
			salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream())), true);
		} catch (IOException e) {
			System.out.println("No puede escuchar en el puerto: " + puerto);
			System.exit(-1);
		}

		this.respuesta = new String[9];
		this.respuesta[0] = "Boleto inválido - Números repetidos";
		this.respuesta[1] = "Boleto inválido - números incorretos (1-49)";
		this.respuesta[2] = "6 aciertos";
		this.respuesta[3] = "5 aciertos + complementario";
		this.respuesta[4] = "5 aciertos";
		this.respuesta[5] = "4 aciertos";
		this.respuesta[6] = "3 aciertos";
		this.respuesta[7] = "Reintegro";
		this.respuesta[8] = "Sin premio";
		//generarCombinacion();
		//imprimirCombinacion();
	}

	/**
	 * @return Debe leer la combinacion de numeros que le envia el cliente
	 */
	public String leerCombinacion() {
		//Pongo aqui generar combinacion e imprimir combinacion para que cada vez que se ejecute el bucle cree una nueva combinacion
		generarCombinacion();
		imprimirCombinacion();

		String respuesta = "";

		try {
			// Leo la combinacion que el usuario ha mandado desdde el cliente
			respuesta = entrada.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Retorno la respuesta
		return respuesta;
	}

	/**
	 * @return Debe devolver una de las posibles respuestas configuradas
	 */
	public String comprobarBoleto(String combinacionCliente) {

		// Guardo en un array de string la informacion
		String[] arrayCombinacionClienteString = combinacionCliente.split(" ");
		// La paso a un array de int
		int[] arrayCombinacionClienteInt = new int[arrayCombinacionClienteString.length];

		// En un bucle for añado todos los strings, pasandolos a ints y añadiendolos
		for (int i = 0; i < arrayCombinacionClienteString.length; i++) {
			arrayCombinacionClienteInt[i] = Integer.parseInt(arrayCombinacionClienteString[i]);
		}

		// Ordeno el atributo que contiene la combinación del servidor
		Arrays.sort(this.combinacion);

		// Creo un mapa para contar las veces que sale cada combinacion
		HashMap<Integer, Integer> mapaNumerosCoincidencias = new HashMap<>();

		// En un bucle for, añado todos los numeros que salen y los establezco en 0 para que hayan salido 0 veces
		for (int i = 0; i < arrayCombinacionClienteInt.length; i++) {
			mapaNumerosCoincidencias.put(arrayCombinacionClienteInt[i], 0);
		}

		
		for (int i = 0; i < arrayCombinacionClienteInt.length; i++) {
			for (int j = 0; j < arrayCombinacionClienteInt.length; j++) {
				if (arrayCombinacionClienteInt[i] == this.combinacion[j]) {
					int numeroVecesMapa = mapaNumerosCoincidencias.get(arrayCombinacionClienteInt[i]);
					mapaNumerosCoincidencias.put(arrayCombinacionClienteInt[i], (numeroVecesMapa + 1));
				}
			}
		}

		String respuesta = "";

		boolean numerosIncorrectos = false;

		for (int i = 0; i < arrayCombinacionClienteInt.length; i++) {
			if (arrayCombinacionClienteInt[i] > 49 || arrayCombinacionClienteInt[i] < 1) {
				numerosIncorrectos = true;
			}
		}

		int coincidencias = 0;
		boolean complementario = false;
		boolean reintegro = false;

		for (int i = 0; i < mapaNumerosCoincidencias.size(); i++) {
			
			if (mapaNumerosCoincidencias.get(arrayCombinacionClienteInt[i]) > 0) {
				coincidencias++;
				System.out.println(coincidencias);
			}

			if (arrayCombinacionClienteInt[i] == this.complementario) {
				complementario = true;
			}

			if (arrayCombinacionClienteInt[i] == this.reintegro) {
				reintegro = true;
			}

		}

		if (mapaNumerosCoincidencias.size() < 6) {
			respuesta = this.respuesta[0];
		} else if (numerosIncorrectos) {
			respuesta = this.respuesta[1];
		} else if (coincidencias == 6) {
			respuesta = this.respuesta[2];
		} else if (coincidencias == 5 && complementario) {
			respuesta = this.respuesta[3];
		} else if (coincidencias == 5) {
			respuesta = this.respuesta[4];
		} else if (coincidencias == 4) {
			respuesta = this.respuesta[5];
		} else if (coincidencias == 3) {
			respuesta = this.respuesta[6];
		} else if (reintegro) {
			respuesta = this.respuesta[7];
		} else if (coincidencias == 0 || coincidencias == 1 || coincidencias == 2) {
			respuesta = this.respuesta[8];
		}

		return respuesta;
	}

	/**
	 * @param respuesta se debe enviar al ciente
	 */
	public void enviarRespuesta(String respuesta) {
		salida.println(respuesta);
	}

	public boolean recibirSiNo() {

		String recibido = "";
		try {
			recibido = entrada.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (recibido.equals("n")) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * Cierra el servidor
	 */
	public void finSesion() {
		try {
			salida.close();
			entrada.close();
			socketCliente.close();
			socketServidor.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("-> Servidor Terminado");
	}

	/**
	 * Metodo que genera una combinacion. NO MODIFICAR
	 */
	private void generarCombinacion() {
		Set<Integer> numeros = new TreeSet<Integer>();
		Random aleatorio = new Random();
		while (numeros.size() < 6) {
			numeros.add(aleatorio.nextInt(49) + 1);
		}
		int i = 0;
		this.combinacion = new int[6];
		for (Integer elto : numeros) {
			this.combinacion[i++] = elto;
		}
		this.reintegro = aleatorio.nextInt(49) + 1;
		this.complementario = aleatorio.nextInt(49) + 1;
	}

	/**
	 * Metodo que saca por consola del servidor la combinacion
	 */
	private void imprimirCombinacion() {
		System.out.print("Combinación ganadora: ");
		for (Integer elto : this.combinacion)
			System.out.print(elto + " ");
		System.out.println("");
		System.out.println("Complementario:       " + this.complementario);
		System.out.println("Reintegro:            " + this.reintegro);
	}

}
