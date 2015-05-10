import java.io.*;
import java.util.*;
import javax.comm.*;

public class ComportCommunication {
    
    private String inputPortA;
    private String inputPortB;
    private boolean reset;
    
    /* Objects for communication */
    /** Identify a port */
    private static CommPortIdentifier portIdentifier;
    /** List with all ports */
    private static Enumeration portList;
    /** Board Communication */
    private SerialPort serialPort = null;
    private InputStream input = null;
    private OutputStream output = null;
    
    /** Specifications for connection */
    private String port = "";
    private final int baudRate = 4800;
    private final int dataBit= SerialPort.DATABITS_8;
    private final int stopBit= SerialPort.STOPBITS_1;
    private final int parity = SerialPort.PARITY_NONE;
    
    /**
     * Constructor
     * @param port
     */
    public ComportCommunication(String port)    {
        this.port = port;
        portList = CommPortIdentifier.getPortIdentifiers();
        
        while (portList.hasMoreElements()) {
            portIdentifier = (CommPortIdentifier) portList.nextElement();
            
            try {
                if(portIdentifier.getName().equals(port))   {
                    serialPort = (SerialPort) portIdentifier.open("serial", 1000);
                    serialPort.setSerialPortParams(baudRate, dataBit, stopBit, parity);
                }
            } catch(PortInUseException e) {} catch(UnsupportedCommOperationException e) {}
        }
    }
    
    public void communicateWithComport(String trisA, String portA, String trisB, String portB) {
        /* Save parameters in charcter array after parsing to ASCII */
        char[] trisACharacter = parseHexToAscii(trisA.toCharArray());
        char[] portACharacter = parseHexToAscii(portA.toCharArray());
        char[] trisBCharacter = parseHexToAscii(trisB.toCharArray());
        char[] portBCharacter = parseHexToAscii(portB.toCharArray());
        
        /* Cast char to int */
        /* TrisA / PortA */
        int trisAHighNibble = (int)trisACharacter[0];
        int trisALowNibble = (int)trisACharacter[1];
        int portAHighNibble = (int)portACharacter[0];
        int portALowNibble = (int)portACharacter[1];
        
        /* TrisB / PortB */
        int trisBHighNibble = (int)trisBCharacter[0];
        int trisBLowNibble = (int)trisBCharacter[1];
        int portBHighNibble = (int)portBCharacter[0];
        int portBLowNibble = (int)portBCharacter[1];
        
        /* Initialising input and output */
        try {
            output = serialPort.getOutputStream();
            input = serialPort.getInputStream();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        try {
            /* sending */
            output.write(trisAHighNibble);
            output.write(trisALowNibble);
            output.write(portAHighNibble);
            output.write(portALowNibble);
            output.write(trisBHighNibble);
            output.write(trisBLowNibble);
            output.write(portBHighNibble);
            output.write(portBLowNibble);
            /* Carige Return */
            output.write(13);
            output.flush();
            output.close();
            
            /* Receiving data from board */
            /* PortA */
            char inputPortAHighNibble = parseAsciiToHex((char)input.read());
            char inputPortALowNibble = parseAsciiToHex((char)input.read());
            
            /* PortB */
            char inputPortBHighNibble = parseAsciiToHex((char)input.read());
            char inputPortBLowNibble = parseAsciiToHex((char)input.read());
            /* Should be a CR */
            int lastCharacter = input.read();
            
            input.close();
                        
            /* Reset */
            if (inputPortAHighNibble == '1' || inputPortAHighNibble == '0') {
                reset = true;
            } else {
                reset = false;
            }
            
            inputPortA = String.valueOf(inputPortAHighNibble) + String.valueOf(inputPortALowNibble);
            inputPortB = String.valueOf(inputPortBHighNibble) + String.valueOf(inputPortBLowNibble);
        
        } catch (IOException ex) {
            ex.printStackTrace(); 
        }
    }
    
    /**
     * Return value from board of PortA
     * @return inputPortA
     */
    public int getInputPortA() {
        return Integer.parseInt(inputPortA,16);
    }
    
    /**
     * Return value from board of PortB
     * @return inputPortB
     */
    public int getInputPortB() {
        return Integer.parseInt(inputPortB,16);
    }
    
    /**
     * Return true if reset was activated 
     * @return reset
     */
    public boolean resetPushed() {
        return reset;
    }
    
    /**
     * Close serial connection
     */
    public void closeComportConnection() {
        serialPort.close();
    }
    
    private char parseAsciiToHex(char character)    {
        if (character == '?') {
            character = 'f';
        } else if (character == '>') {
            character = 'e';
        } else if (character == '=') {
            character = 'd';
        } else if (character == '<') {
            character = 'c';
        } else if (character == ';') {
            character = 'b';
        } else if (character == ':') {
            character = 'a';
        } else {
            character = character;
        }
        return character;
    }
    
    private char[] parseHexToAscii(char[] characters) {
        for (int i = 0; i < characters.length; i++) {
            if (characters[i] == 'A' || characters[i] == 'a') {
                characters[i] = ':';
            } else if (characters[i] == 'B' || characters[i] == 'b') {
                characters[i] = ';';
            } else if (characters[i] == 'C' || characters[i] == 'c') {
                characters[i] = '<';
            } else if (characters[i] == 'D' || characters[i] == 'd') {
                characters[i] = '=';
            } else if (characters[i] == 'E' || characters[i] == 'e') {
                characters[i] = '>';
            } else if (characters[i] == 'F' || characters[i] == 'f') {
                characters[i] = '?';
            } else {
                characters[i] = characters[i];
            }
        }
        return characters;
    }
}