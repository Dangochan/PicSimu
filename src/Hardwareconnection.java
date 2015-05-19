import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;

public class Hardwareconnection {

	private static storage sto = storage.getInstance();
	private static OutputStreamWriter outstream;
	private static InputStreamReader instream;
	private static CommPortIdentifier portNumber;
	private static SerialPort serialPort;
	private static char CR = '\r';

	// Seriele Verbindung erstellen
	public static boolean initializeConnection(String usedPort) {
		boolean found = false;
		try {
			Enumeration portList = CommPortIdentifier.getPortIdentifiers();
			// Com-Ports suchen
			while (portList.hasMoreElements()) {
				portNumber = (CommPortIdentifier) portList.nextElement();
				if (portNumber.getPortType() == CommPortIdentifier.PORT_SERIAL) {
					if (portNumber.getName().equals(usedPort)) { // Com-Port
																	// gefunden?
						found = true;
						break;
					}
				}
			}
			if (!found) {
				return false;
			}
			// Parameter für Datenübertragung
			serialPort = (SerialPort) portNumber.open("PICSIM", 2000);
			serialPort.setSerialPortParams(4800, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			instream = new InputStreamReader(serialPort.getInputStream());
			outstream = new OutputStreamWriter(serialPort.getOutputStream());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void sendData() throws Exception {
		String tris_a = encodeData(sto.getDataStorage(0x85)); // String zusammen
																// setzen
		String port_a = encodeData(sto.getDataStorage(0x05));
		String tris_b = encodeData(sto.getDataStorage(0x86));
		String port_b = encodeData(sto.getDataStorage(0x06));

		String send = tris_a + port_a + tris_b + port_b;
		write(send + CR);
	}

	public static void write(String s) throws Exception { // Sendet Daten an den
															// Outputstream
		outstream.write(s);
		outstream.flush(); // Outputstream leeren
	}

	public static ArrayList<Integer> readData() throws Exception {
		int n, i;
		char c = 0;
		String answer = new String("");
		int index = 5; // Es müssen 4 Bytes eingelesen werden --> TrisA/B,
						// PortA/B
		while (c != CR && index > 0 && instream.ready()) { // Alles lesen bis CR
			// CR markiert das Ende einer Datenübertragung
			n = instream.read();
			if (n != -1) { // n == -1 --> Ende des Streams erreicht
				c = (char) n;
				answer += c; // String zusammenbauen
				index--;
			}
		}

		if (index <= 0 && c != CR) { // Überlauf abfangen
			return null;
		}
		delay(1); // Programm wäre zu schnell für Datenübertragung
		ArrayList<Integer> decodedValues = new ArrayList<Integer>(); // Antwort
																		// als
																		// Liste
																		// implementiert
		decodedValues = decodeData(answer);
		if (decodedValues.size() > 0) {
			return decodedValues;
		} else {
			return null;
		}
	}

	private static void delay(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}

	public static void close() throws Exception {
		try {
			serialPort.close();
		} catch (Exception e) {
		}
	}

	private static String encodeData(int b) { // Codierung siehe Pic-View doku
		char c1 = (char) (0x30 + ((b & 0xF0) >> 4)); // Oberes Halbbyte
		char c2 = (char) (0x30 + (b & 0x0F)); // Unteres Halbbyte
		return "" + c1 + c2;
	}

	private static ArrayList<Integer> decodeData(String s) {
		// Oberes Halbbyte auf 0 setzen, da die 3 im Zuge der Codierung
		// eingefügt wurde
		int part1 = s.charAt(0) - 0x32; // RA5 rausmaskieren?!
		int part2 = s.charAt(1) - 0x30;
		int part3 = s.charAt(2) - 0x30;
		int part4 = s.charAt(3) - 0x30;
		ArrayList<Integer> tokens = new ArrayList<Integer>();
		if (part1 >= 0 && part2 >= 0 && part3 >= 0 && part4 >= 0
				&& part1 <= 0xF && part2 <= 0xF && part3 <= 0xF && part4 <= 0xF) {
			int a = (((int) part1 & 0x0F) << 4) | ((int) part2 & 0x0F);// Halbbytes
																		// zusammenfügen
			tokens.add(a);
			int b = (((int) part3 & 0x0F) << 4) | ((int) part4 & 0x0F);
			tokens.add(b);
		}
		if (tokens.size() > 0) {
			return tokens;
		} else {
			return null;
		}
	}
}