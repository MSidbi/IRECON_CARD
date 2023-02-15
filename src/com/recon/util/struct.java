package com.recon.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class struct{
    public static String[] unpack(char[] packet, byte[] raw){
        String[] result = new String[packet.length];

        int pos = 0;
        int Strindex = 0;

        for (int x = 0; x < packet.length; x++){

            char type = packet[x];
            if (type == 'x'){
                pos += 1;
                continue;
            }
            else if (type == 'c'){
                char c = (char) (raw[pos] & 0xFF);
                result[Strindex] = Character.toString(c);
                Strindex += 1;
                pos += 1;
            }
            else if (type == 'h'){
                ByteBuffer bb = ByteBuffer.allocate(2);
                bb.order(ByteOrder.LITTLE_ENDIAN); //
                bb.put(raw[pos]);
                bb.put(raw[pos+1]);
                short shortVal = bb.getShort(0);
                result[Strindex] = Short.toString(shortVal);
                pos += 2;
                Strindex += 1;
            }
            else if (type == 's'){
                String s = "";

                while (raw[pos] != (byte)0x00){
                    char c = (char) (raw[pos] & 0xFF);
                    s += Character.toString(c);
                    pos += 1;
                }
                result[Strindex] = s;
                Strindex += 1;
                pos += 1;
            }
            else if (type == 'b'){
                Byte p = raw[pos];
                result[Strindex] = Integer.toString(p.intValue());
                Strindex += 1;
                pos += 1;
            }
        }
        
        for(int sss=0; sss< result.length;sss++){
        	// System.out.println(result[sss]);
        }
       // System.out.println("");
        
        
        return result;
    }
    
    public static int[] unpack(final byte[] byte_array) {
        final int[] integerReadings = new int[byte_array.length / 2];
        for(int counter = 0, integerCounter = 0; counter < byte_array.length;) {
            integerReadings[integerCounter] = convertTwoBytesToInteger(byte_array[counter], byte_array[counter + 1]);
            counter += 2;
            integerCounter++;
        }
        return integerReadings;
    }

    private static int convertTwoBytesToInteger(final byte byte1, final byte byte2) {
        final int unsignedInteger1 = getUnsignedInteger(byte1);
        final int unsignedInteger2 = getUnsignedInteger(byte2);
        return unsignedInteger1 * 256 + unsignedInteger2;
    }

    private static int getUnsignedInteger(final byte b) {
        int unsignedInteger = b;
        if(b < 0) {
            unsignedInteger = b + 256;
        }
        return unsignedInteger;
    }
}