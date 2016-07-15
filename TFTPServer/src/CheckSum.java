import java.io.FileInputStream;
import java.security.MessageDigest;


public class CheckSum {
	static String getChecksum(String fileName) {
		StringBuffer sb = new StringBuffer("");
		try {
			String datafile = fileName;
			//use SHA1 to calculate checksum
			MessageDigest md = MessageDigest.getInstance("SHA1");
			FileInputStream fis = new FileInputStream(datafile);
			byte[] dataBytes = new byte[1024];

			int nread = 0;

			while ((nread = fis.read(dataBytes)) != -1) {
				md.update(dataBytes, 0, nread);
			}
			fis.close();

			byte[] mdbytes = md.digest();

			// convert the byte to hex format
			for (int i = 0; i < mdbytes.length; i++) {
				sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
			}

		} catch (Exception e){System.out.println("Generate Checksum Failed: "+e.getMessage());}
		
		return sb.toString();
	}
	}
