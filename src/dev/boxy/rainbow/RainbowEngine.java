package dev.boxy.rainbow;

import gnu.io.*;

import java.io.*;
import java.util.*;

public class RainbowEngine implements SerialPortEventListener {
	
	SerialPort serialPort;
	
	private static final int TIME_OUT = 2000;
	
	private static final String PORT_NAMES[] = { 
			"/dev/tty.usbmodem621", // Mac OS X
			"/dev/tty.usbmodem411", // Mac OS X
			"/dev/ttyUSB0", // Linux
			"COM3", // Windows
			};
	

	private BufferedReader input;
	private DataOutputStream output;
	
	// Baud rate agreed upon between Arduino and this program
	private static final int DATA_RATE = 115200;

	// Protocol messages
	private static final byte LINE = 1;
	private static final byte FLUSH = 2;
	private static final byte LINE_LENGTH = 3;
	
	// Number of LEDs on the strip
	private static final int LEDS = 78;
	
	// Adjust this number to find a rate that the Arduino can keep up with. Lower is faster.
	private static final int MS_SLEEP_BETWEEN_FRAMES = 5;
	
	// To keep track of FPS, set to true and it will print in the console
	private static final boolean SHOW_FPS = false;

	// A key feature is that this is <= 256 bytes so the serial buffer on the Arduino does not overflow!!!
	private static final int SIZE = LEDS * 3;

	public void initialize() {
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		// iterate through, looking for the port
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}

		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output = new DataOutputStream(serialPort.getOutputStream());

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String line;
				
				while ((line = input.readLine()) != null) {
					System.out.println("LINE: " + line);
				}
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
	}

	byte bytes[];
	
	public void loadDat(String file) throws Exception {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));

		File f = new File(file);
		int flength = (int) f.length();
		bytes = new byte[flength];
		in.read(bytes, 0, flength);
	}

	public int sendOutDat() throws Exception {
		int sends = 0;

		output.write(new byte[] { LINE_LENGTH, (byte) SIZE });
		output.flush();
		
		for (int i = 0; i < bytes.length; i += SIZE) {
			System.out.println("WRITING chunk " + i);

			output.writeByte(LINE);
			output.write(bytes, i, SIZE);
			output.flush();

			output.write(FLUSH);
			output.flush();

			// Necessary to have this to ensure the Arduino and this engine are synchronized
			Thread.sleep(MS_SLEEP_BETWEEN_FRAMES);

			sends++;
		}

		return sends;
	}

	public static void main(String[] args) throws Exception {
		RainbowEngine main = new RainbowEngine();
		main.initialize();
		
		// Necessary sleep, otherwise will not work!
		Thread.sleep(2000);
		
		int sends = 0;
		System.out.println("Started");
		long time = System.currentTimeMillis();

		main.loadDat(args[0]);
		
		while (true) {
			sends += main.sendOutDat();
			if (SHOW_FPS) {
				System.out.printf("time %d sends %d sends per second %.2f\n", time, sends, (double) sends / (time / 1000.0));
			}
		}
	}
}


