package com.recon.util.chargeback;



public class StringManipulation {
	
	 private static final int PAD_LIMIT = 8192;
	 
	 public static String leftPad(String str, int size, char padChar) {
	      if (str == null) {
	          return null;
	      }
	      int pads = size - str.length();
	      if (pads <= 0) {
	          return str; // returns original String when possible
	      }
	      if (pads > PAD_LIMIT) {
	          return leftPad(str, size, String.valueOf(padChar));
	      }
	      return padding(pads, padChar).concat(str);
	  }
	  public static String leftPad(String str, int size, String padStr) {
	      if (str == null) {
	          return null;
	      }
	      if (isEmpty(padStr)) {
	          padStr = " ";
	      }
	      int padLen = padStr.length();
	      int strLen = str.length();
	      int pads = size - strLen;
	      if (pads <= 0) {
	          return str; // returns original String when possible
	      }
	      if (padLen == 1 && pads <= PAD_LIMIT) {
	          return leftPad(str, size, padStr.charAt(0));
	      }

	      if (pads == padLen) {
	          return padStr.concat(str);
	      } else if (pads < padLen) {
	          return padStr.substring(0, pads).concat(str);
	      } else {
	          char[] padding = new char[pads];
	          char[] padChars = padStr.toCharArray();
	          for (int i = 0; i < pads; i++) {
	              padding[i] = padChars[i % padLen];
	          }
	          return new String(padding).concat(str);
	      }
	  }
	 private static String padding(int repeat, char padChar) throws IndexOutOfBoundsException {
	      if (repeat < 0) {
	          throw new IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat);
	      }
	      final char[] buf = new char[repeat];
	      for (int i = 0; i < buf.length; i++) {
	          buf[i] = padChar;
	      }
	      return new String(buf);
	  }
	 
	  public static boolean isEmpty(String str) {
	      return str == null || str.length() == 0;
	  }
	
	
	 public static String padRight(String s, int n) {
		    return String.format("%1$-" + n + "s", s);
		  }
	 
	 public static String padLeft(String s, int n) {
		    return String.format("%1$#" + n + "s", s);
		  }

}
