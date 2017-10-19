import java.util.BitSet;

public class MyBitSet extends BitSet {
	
	MyBitSet(String s) {
		super();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			for (int j = 0; j < Character.SIZE; j++) {
				this.set(Character.SIZE * i + j, ((int)c >> j & 1) > 0);
			}
		}
	}
	
	MyBitSet() {
		super();
	}
	
	@Override
	public String toString() {
		int length = (this.length() + Character.SIZE - 1) / (Character.SIZE);
		char[] str = new char[length];
		for (int i = 0; i < length; i++) {
			str[i] = (char) this.get(Character.SIZE * i, Character.SIZE * (i+1))
					.toLongArray()[0];
		}
		return new String(str);
	}
}
