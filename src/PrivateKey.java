import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class PrivateKey {
		BigInteger[] seq;
		BigInteger q;
		BigInteger r;

		PrivateKey(int n) {
			seq = new BigInteger[n];
		}

		public static PrivateKey loadPrivateKey(File input) throws FileNotFoundException {
			Scanner in = new Scanner(input);
			BigInteger q = in.nextBigInteger();
			BigInteger r = in.nextBigInteger();
			int n = in.nextInt();
			PrivateKey key = new PrivateKey(n);
			key.q = q;
			key.r = r;
			for (int i = 0; i < n; i++) {
				key.seq[i] = in.nextBigInteger();
			}
			in.close();
			return key;
		}

		public static PrivateKey loadPrivateKey(URL url) throws IOException {
			URLConnection conn = url.openConnection();
			Scanner in = new Scanner(conn.getInputStream());
			BigInteger q = in.nextBigInteger();
			BigInteger r = in.nextBigInteger();
			int n = in.nextInt();
			PrivateKey key = new PrivateKey(n);
			key.q = q;
			key.r = r;
			for (int i = 0; i < n; i++) {
				key.seq[i] = in.nextBigInteger();
			}
			in.close();
			return key;
		}

}
