package ch.epfl.chacun;

/**
 * Represents a base 32 encoding/decoding utility
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public final class Base32 {
    /**
     * The Base32 alphabet used for encoding/decoding
     */
    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    /**
     * The number of bits per character in Base32
     */
    private static final int BITS_PER_CHAR = 5;

    /**
     * The number of bits for two characters in Base32
     */
    private static final int BITS_FOR_TWO_CHARS = 10;

    /**
     * The maximum number of characters in our Base32 strings
     */
    private static final int MAX_NUMBER_OF_CHAR = 2;

    /**
     * Private constructor to prevent instantiation
     */
    private Base32() {
    }

    /**
     * Checks if a string is a valid Base32 string
     *
     * @param stringToTest the string to test
     * @return true if the string is a valid Base32 string, false otherwise
     */
    public static boolean isValid(String stringToTest) {
        return stringToTest.chars().allMatch(c -> ALPHABET.indexOf(c) != -1);
    }

    /**
     * Encodes the 5 least significant bits of a number into a Base32 character
     *
     * @param value the number to encode
     * @return the Base32 String representation of the 5 least significant bits of the number
     */
    public static String encodeBits5(int value) {
        Preconditions.checkArgument(value >= 0 && value < (1 << BITS_PER_CHAR));
        return String.valueOf(ALPHABET.charAt(value));
    }

    /**
     * Encodes the 10 least significant bits of a number into a Base32 string of 2 characters
     *
     * @param value the number to encode
     * @return the Base32 String representation of the 10 least significant bits of the number
     */
    public static String encodeBits10(int value) {
        Preconditions.checkArgument(value >= 0 && value < (1 << BITS_FOR_TWO_CHARS));
        return encodeBits5(value >> BITS_PER_CHAR) + encodeBits5(value);
    }

    /**
     * Decodes a Base32 string of length 1 or 2 into a number
     *
     * @param encoded the Base32 string to decode
     * @return the number represented by the Base32 string
     * @throws IllegalArgumentException if the string is empty or has more than 2 characters
     */
    public static int decode(String encoded) {
        Preconditions.checkArgument(!encoded.isEmpty()
                && encoded.length() <= MAX_NUMBER_OF_CHAR
                && isValid(encoded)
        );

        return encoded.length() == MAX_NUMBER_OF_CHAR ? decodeTwoChars(encoded) : decodeOneChar(encoded.charAt(0));
    }

    /**
     * Decodes a Base32 character into a number
     *
     * @param c the Base32 character to decode
     * @return the number represented by the Base32 character
     */
    private static int decodeOneChar(char c) {
        return ALPHABET.indexOf(c);
    }

    /**
     * Decodes a Base32 string of length 2 into a number
     *
     * @param encoded the Base32 string to decode
     * @return the number represented by the Base32 string
     */
    private static int decodeTwoChars(String encoded) {
        return (decodeOneChar(encoded.charAt(0)) << BITS_PER_CHAR) + decodeOneChar(encoded.charAt(1));
    }
}
