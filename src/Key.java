import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Scanner;


public class Key {
	PrivateKey privateKey;
	private BigInteger[] publicKey;
	Key(BigInteger[] publicKey, PrivateKey privateKey) {
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	BigInteger[] getPublicKey() {
		return publicKey.clone();
	}

	public void saveKey(File dir) throws FileNotFoundException {
		if (publicKey != null && privateKey != null) {
			dir.mkdirs();
			File publicKeyFile = new File(dir.getAbsolutePath() + "/mh.pubkey.txt");
			File privateKeyFile = new File(dir.getAbsolutePath() + "/mh.privatekey.txt");
			PrintWriter out = new PrintWriter(publicKeyFile);
			out.println(publicKey.length);
			Arrays.stream(publicKey).forEach(out::println);
			out.close();
			out = new PrintWriter (privateKeyFile);
			out.println(privateKey.q);
			out.println(privateKey.r);
			out.println();
			out.println(publicKey.length);
			Arrays.stream(privateKey.seq).forEach(out::println);
			out.close();
		}
		return;
	}

	public static BigInteger[] loadPublicKey(File input) throws FileNotFoundException {
		Scanner in = new Scanner(input);
		int n = in.nextInt();
		BigInteger[] ans = new BigInteger[n];
		for (int i = 0; i < n; i++) {
			ans[i] = in.nextBigInteger();
		}
		in.close();
		return ans;
	}

	public static BigInteger[] loadPublicKey(URL url) throws IOException {
		URLConnection conn = url.openConnection();
		Scanner in = new Scanner(conn.getInputStream());
		int n = in.nextInt();
		BigInteger[] ans = new BigInteger[n];
		for (int i = 0; i < n; i++) {
			ans[i] = in.nextBigInteger();
		}
		in.close();
		return ans;
	}

}
