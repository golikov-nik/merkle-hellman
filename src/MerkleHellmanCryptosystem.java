import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.IntStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class MerkleHellmanCryptosystem {

	public static Key generateKeys(int n, Random rnd) {
		BigInteger sum = randomBigInteger(n, rnd);
		PrivateKey privateKey = new PrivateKey(n);
		privateKey.seq[0] = sum;
		for (int i = 1; i < n; i++) {
			BigInteger toAdd = randomBigInteger(sum.bitLength() + 1, rnd);
			sum = sum.add(toAdd);
			privateKey.seq[i] = toAdd;
		}
		BigInteger q = BigInteger.probablePrime(sum.bitLength() + 1, rnd);
		BigInteger r;
		do {
			r = BigInteger.probablePrime(sum.bitLength(), rnd);
		} while (!r.gcd(q).equals(BigInteger.ONE) && r.compareTo(BigInteger.ONE) < 0);
		privateKey.q = q;
		privateKey.r = r;
		BigInteger[] publicKey = Arrays.stream(privateKey.seq)
				.map(e -> e.multiply(privateKey.r).mod(q))
				.toArray(BigInteger[]::new);
		return new Key(publicKey, privateKey);
	}

	public static BigInteger encrypt(String message, BigInteger[] publicKey) {
		MyBitSet bitSet = new MyBitSet(message);
		assert message.length() * Character.SIZE <= publicKey.length;
		return IntStream.range(0, message.length() * Character.SIZE)
				.filter(bitSet::get)
				.mapToObj(i -> publicKey[i])
				.reduce(BigInteger::add)
				.orElse(BigInteger.ZERO);
	}

	public static String decrypt(BigInteger message, PrivateKey key) {
		BigInteger sum = message.multiply(key.r.modInverse(key.q)).mod(key.q);
		MyBitSet bitSet = new MyBitSet();
		for (int i = key.seq.length - 1; i >= 0; i--) {
			if (key.seq[i].compareTo(sum) <= 0) {
				sum = sum.subtract(key.seq[i]);
				bitSet.set(i);
			}
		}
		return bitSet.toString();
	}
	
	private static BigInteger randomBigInteger(int length, Random r) {
		BigInteger n;
		do {
			n = new BigInteger(length, r);
		} while (n.bitLength() != length);
		return n;
	}

	public static void main(String[] args) throws IOException{
		if (args.length == 0) {
			System.out.println("Usage: <mode> <args..>");
			System.exit(1);
		}
		Options options = new Options();
		if (args[0].equals("keygen") || args[0].equals("kg")) {
			runKeygen(options, args);
		} else if (args[0].equals("encrypt") || args[0].equals("en")) {
			runEncrypter(options, args);
		} else if (args[0].equals("decrypt") || args[0].equals("de")) {
			runDecrypter(options, args);
		} else {
			System.out.println("Available modes:");
			System.out.println("1. Key Generator [kg, keygen]");
			System.out.println("2. Encrypter [en, encrypt]");
			System.out.println("3. Decrypter [de, decrypt]");
			return;
		}
	}
	
	private static void runDecrypter(Options options, String[] args) throws IOException {
		Option msg = new Option("s", true , "string to decrypt");
		options.addOption(msg);
		Option f = new Option("i", true, "input file to decrypt");
		options.addOption(f);
		Option keyFile = new Option("f", "key", true, "file with private key");
		options.addOption(keyFile);
		Option link = new Option("url", true, "url of the private key");
		options.addOption(link);
		Option outOpt = new Option("out", true, "file to write encrypted string to");
		options.addOption(outOpt);
		Option help = new Option("h", "help-all", false, "print help");
		options.addOption(help);
		CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("decrypter", options);
            System.exit(1);
            return;
        }
        if (cmd.hasOption("h")) {
        	formatter.printHelp("decrypter", options);
        	System.exit(0);
        	return;
        }
        String message;
        if (cmd.hasOption("s") && cmd.hasOption("i")) {
        	System.out.println("Your input must be either a file or string");
        	System.exit(1);
        	return;
        } else if (cmd.hasOption("s")) {
        	message = cmd.getOptionValue("s").trim();
        } else if (cmd.hasOption("i")) {
        	File inp = new File(cmd.getOptionValue("i"));
        	message = readFile(inp).trim();
        } else {
        	System.out.println("At least one of the parameters -s and -i must be set.");
        	formatter.printHelp("decrypter", options);
        	System.exit(1);
        	return;
        }
		if (cmd.hasOption("f") && cmd.hasOption("url")) {
			System.out.println("Your private key must be either a file or a url");
			System.exit(1);
		} else if (cmd.hasOption("f")) {
			File in = new File(cmd.getOptionValue("f"));
			String decrypted = decrypt(new BigInteger(message), PrivateKey.loadPrivateKey(in));
			if (cmd.hasOption("out")) {
				PrintWriter out = new PrintWriter(new File(cmd.getOptionValue("out")));	
				out.print(decrypted);
				out.close();
			} else {
			System.out.print(decrypted);
			}
		} else if (cmd.hasOption("url")) {
			URL url = new URL(cmd.getOptionValue("url"));
			String decrypted = decrypt(new BigInteger(message), PrivateKey.loadPrivateKey(url));
			if (cmd.hasOption("out")) {
				PrintWriter out = new PrintWriter(new File(cmd.getOptionValue("out")));	
				out.print(decrypted);
				out.close();
			} else {
			System.out.print(decrypted);
			}
		} else {
			System.out.println("At least one of the parameters -f and -url must be set.");
			formatter.printHelp("decrypter", options);
            System.exit(1);
		}
	}

	private static void runEncrypter(Options options, String[] args) throws IOException {
		Option msg = new Option("s", true , "string to encrypt");
		options.addOption(msg);
		Option f = new Option("i", true, "input file to encrypt");
		options.addOption(f);
		Option keyFile = new Option("f", "key", true, "file with public key");
		options.addOption(keyFile);
		Option link = new Option("url", true, "url of the public key");
		options.addOption(link);
		Option outOpt = new Option("out", true, "file to write encrypted string to");
		options.addOption(outOpt);
		Option help = new Option("h", "help-all", false, "print help");
		options.addOption(help);
		CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("encrypter", options);
            System.exit(1);
            return;
        }
        if (cmd.hasOption("h")) {
        	formatter.printHelp("encrypter", options);
        	System.exit(0);
        	return;
        }
        String message;
        if (cmd.hasOption("s") && cmd.hasOption("i")) {
        	System.out.println("Your input must be either a file or string");
        	System.exit(1);
        	return;
        } else if (cmd.hasOption("s")) {
        	message = cmd.getOptionValue("s");
        } else if (cmd.hasOption("i")) {
        	File inp = new File(cmd.getOptionValue("i"));
        	message = readFile(inp);
        } else {
        	System.out.println("At least one of the parameters -s and -i must be set.");
        	formatter.printHelp("encrypter", options);
        	System.exit(1);
        	return;
        }
		if (cmd.hasOption("f") && cmd.hasOption("url")) {
			System.out.println("Your public key must be either a file or a url");
			System.exit(1);
		} else if (cmd.hasOption("f")) {
			File in = new File(cmd.getOptionValue("f"));
			BigInteger encrypted = encrypt(message, Key.loadPublicKey(in));
			if (cmd.hasOption("out")) {
				PrintWriter out = new PrintWriter(new File(cmd.getOptionValue("out")));	
				out.print(encrypted);
				out.close();
			} else {
			System.out.print(encrypted);
			}
		} else if (cmd.hasOption("url")) {
			URL url = new URL(cmd.getOptionValue("url"));
			BigInteger encrypted = encrypt(message, Key.loadPublicKey(url));
			if (cmd.hasOption("out")) {
				PrintWriter out = new PrintWriter(new File(cmd.getOptionValue("out")));	
				out.print(encrypted);
				out.close();
			} else {
			System.out.print(encrypted);
			}
		} else {
			System.out.println("At least one of the parameters -f and -url must be set.");
			formatter.printHelp("encrypter", options);
            System.exit(1);
		}
	}

	private static String readFile(File inp) throws FileNotFoundException {
		Scanner in = new Scanner(inp);
		StringBuilder sb = new StringBuilder();
		while (in.hasNextLine()) {
			sb.append(in.nextLine());
			if (in.hasNextLine()) {
				sb.append("\n");
			}
		}
		in.close();
		return sb.toString();
	}

	private static void runKeygen(Options options, String[] args) throws FileNotFoundException {
		Option length = new Option("len", true , "maximal possible length to be encrypted with your key (default value: 256)");
		options.addOption(length);
		Option outDir = new Option("out", true, "output directory");
		options.addOption(outDir);
		Option help = new Option("h", "help-all", false, "print help");
		options.addOption(help);
		CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("keygen", options);
            System.exit(1);
            return;
        }
        if (cmd.hasOption("h")) {
        	formatter.printHelp("keygen", options);
        	System.exit(0);
        	return;
        }
        int n = Integer.parseInt(cmd.getOptionValue("len", "256"));
        File out = new File(cmd.getOptionValue("out", ""));
        Key key = generateKeys(n, new Random());
        key.saveKey(out);
	}
}