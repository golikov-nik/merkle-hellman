

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class MerkleHellmanTest {
	Key key;
	
	@Before
	public void setUp() {
		key = MerkleHellmanCryptosystem.generateKeys(16, new Random());
	}
	
	@Test
	public void testBlankString() {
		String message = "";
		BigInteger encrypted = MerkleHellmanCryptosystem.encrypt(message, key.getPublicKey());
		assertEquals(MerkleHellmanCryptosystem.decrypt(encrypted, key.privateKey), message);
	}
	
	@Test
	public void testOneLetterString() {
		String message = "h";
		BigInteger encrypted = MerkleHellmanCryptosystem.encrypt(message, key.getPublicKey());
		assertEquals(MerkleHellmanCryptosystem.decrypt(encrypted, key.privateKey), message);
	}
	
	@Test
	public void testBigString() {
		String message = "h141j4k1kjds09AT";
		BigInteger encrypted = MerkleHellmanCryptosystem.encrypt(message, key.getPublicKey());
		assertEquals(MerkleHellmanCryptosystem.decrypt(encrypted, key.privateKey), message);
	}
	
	@Test
	public void testWrongEncrypted() {
		String message = "1";
		BigInteger encrypted = BigInteger.valueOf(958198515);
		assertNotEquals(MerkleHellmanCryptosystem.decrypt(encrypted, key.privateKey), message);
	}
}
