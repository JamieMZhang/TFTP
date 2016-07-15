
import java.net.*;
import java.security.MessageDigest;
import java.io.*;
import java.util.*;
import java.util.zip.Checksum;

class TFTPclientRRQ {
	protected InetAddress server;
	protected String fileName;
	protected String dataMode;

	public TFTPclientRRQ(InetAddress ip, String name, String mode) {
		server = ip;
		fileName = name;
		dataMode = mode;

		try {// Create socket and open output file
			DatagramSocket sock = new DatagramSocket();
			sock.setSoTimeout(2000); // set time out to 2s

			FileOutputStream outFile = new FileOutputStream("../"+fileName); //parent folder
			// Send request to server
			TFTPread reqPak = new TFTPread(fileName, dataMode);
			reqPak.send(server, 6973, sock);

			TFTPack ack = null;
			InetAddress newIP = server; // for transfer
			int newPort = 0; // for transfer
			int timeoutLimit = 5;
			int testloss = 1; // test only

			// Process the transfer
			System.out.println("Downloading");
			for (int blkNum = 1, bytesOut = 512; bytesOut == 512; blkNum++) {
				while (timeoutLimit != 0) {
					try {
						TFTPpacket inPak = TFTPpacket.receive(sock);
						//check packet type
						if (inPak instanceof TFTPerror) {
							TFTPerror p = (TFTPerror) inPak;
							throw new TftpException(p.message());
						} else if (inPak instanceof TFTPdata) {
							TFTPdata p = (TFTPdata) inPak;

							// visual effect to user
							if (blkNum % 500 == 0) {
								System.out.print("\b.>");
							}
							if (blkNum % 15000 == 0) {
								System.out.println("\b.");
							}

							newIP = p.getAddress();
							// check port num.
							if (newPort != 0 && newPort != p.getPort()) { // wrong port
								continue; // ignore this packet
							}
							newPort = p.getPort();
							// check block num.

							if (/* testloss==20|| */blkNum != p.blockNumber()) { //old data
								// testloss++;
								//System.out.println("@testloss loss blkNum " + blkNum);
								throw new SocketTimeoutException();
							}
							// everything is fine then write to the file
							bytesOut = p.write(outFile);
							// send ack to the server
							ack = new TFTPack(blkNum);
							ack.send(newIP, newPort, sock);
							// testloss++;
							break;
						} else
							throw new TftpException("Unexpected response from server");
					}
					// #######handle time out
					catch (SocketTimeoutException t) {
						// no response to read request, try again
						if (blkNum == 1) { 
							System.out.println("failed to reach the server");
							reqPak.send(server, 6973, sock);
							timeoutLimit--;
						} 
						// no response to the last ack
						else { 
							System.out.println("connecion time out, resend last ack. timeoutlimit left=" + timeoutLimit);
							ack = new TFTPack(blkNum - 1);
							ack.send(newIP, newPort, sock);
							timeoutLimit--;
						}
					}
				}
				if (timeoutLimit == 0) {
					throw new TftpException("Connection failed");
				}
			}
			System.out.println("\nDownload Finished.\nFilename: " + fileName);
			System.out.println("SHA1 Checksum: " + CheckSum.getChecksum("../"+fileName));
			
			outFile.close();
			sock.close();
		} catch (IOException e) {
			System.out.println("IO error, transfer aborted");
			File wrongFile = new File(fileName);
			wrongFile.delete();
		} catch (TftpException e) {
			System.out.println(e.getMessage());
			File wrongFile = new File(fileName);
			wrongFile.delete();
		}
	}
}