

import java.net.*;
import java.io.*;
import java.util.*;


class TFTPclientWRQ {
	protected InetAddress server;
	protected String fileName;
	protected String dataMode;
	public TFTPclientWRQ(InetAddress ip, String name, String mode) {
		server = ip;
		fileName = name;
		dataMode = mode;
		try {
			// Create socket and open output file
			DatagramSocket sock = new DatagramSocket();
			sock.setSoTimeout(2000);
			int timeoutLimit = 5;

			FileInputStream source = new FileInputStream("../"+fileName);

			// Send request to server

			TFTPwrite reqPak = new TFTPwrite(fileName, dataMode);
			reqPak.send(server, 6973, sock);

			TFTPpacket sendRsp = TFTPpacket.receive(sock);
			/*System.out.println("new port " + sendRsp.getPort());*/
			int port = sendRsp.getPort(); // new port for transfer
			//check packet type
			if (sendRsp instanceof TFTPack) {
				TFTPack Rsp = (TFTPack) sendRsp;
				System.out.println("--Server ready--\nUploading");
			} else if (sendRsp instanceof TFTPerror) {
				TFTPerror Rsp = (TFTPerror) sendRsp;
				source.close();
				throw new TftpException(Rsp.message());
			}

			int bytesRead = TFTPpacket.maxTftpPakLen;

			// Process the transfer

			for (int blkNum = 1; bytesRead == TFTPpacket.maxTftpPakLen; blkNum++) {
				TFTPdata outPak = new TFTPdata(blkNum, source);
				/*System.out.println("block no. " + outPak.blockNumber());*/
				bytesRead = outPak.getLength();
				outPak.send(server, port, sock); // send the packet
				
				//visual effect to user
				if(blkNum%500==0){System.out.print("\b.>");}
				if(blkNum%15000==0){System.out.println("\b.");}

				while (timeoutLimit != 0) { // wait for the correct ack
					try {
						TFTPpacket ack = TFTPpacket.receive(sock);
						if (!(ack instanceof TFTPack)) {
							break;
						}

						TFTPack a = (TFTPack) ack;
						 // wrong port number
						if (port != a.getPort()) {
							continue; // ignore this packet
						}
						/*System.out.println("got response from server");*/
						
						// receive ack to former packet, resent
						if (a.blockNumber() != blkNum) {
							System.out.println("Last packet lost, resend packet");
							throw new SocketTimeoutException("Last packet lost, resend packet");
						}
						/*System.out.println("response blk no. " + a.blockNumber());*/
						break;
					} catch (SocketTimeoutException t0) {
						System.out.println("Resend blk " + blkNum);
						outPak.send(server, port, sock); // resend the last
															// packet
						timeoutLimit--;
					}
				} // end of while
				if (timeoutLimit == 0) {
					throw new TftpException("connection failed");
				}

			}
			source.close();
			sock.close();
			
			System.out.println("\nUpload finished!\nFilename: "+fileName);
			System.out.println("SHA1 Checksum: " + CheckSum.getChecksum("../"+fileName));

		} catch (SocketTimeoutException t) {
			System.out.println("No response from sever, please try again");
		} catch (IOException e) {
			System.out.println("IO error, transfer aborted");
		} catch (TftpException e) {
			System.out.println(e.getMessage());
		}
	}

}
