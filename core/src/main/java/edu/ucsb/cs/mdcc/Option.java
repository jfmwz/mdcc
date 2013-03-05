package edu.ucsb.cs.mdcc;


import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.hadoop.hbase.util.Bytes;

public class Option {

    private String key;
    private ByteBuffer value;
    private long oldVersion;
    private boolean classic;

    public Option(String key, ByteBuffer value, long oldVersion, boolean classic) {
        this.key = key;
        this.value = value;
        this.oldVersion = oldVersion;
        this.classic = classic;
    }

    public String getKey() {
        return key;
    }

    public ByteBuffer getValue() {
        return value;
    }

    public long getOldVersion() {
        return oldVersion;
    }

    public boolean isClassic() {
        return classic;
    }

    public void setClassic() {
        classic = true;
    }
    
    public ByteBuffer toBytes(){
    	ByteBuffer serialized;
    	byte[] keyBytes = Bytes.toBytes(key);
    	byte[] keyLengthBytes = Bytes.toBytes(keyBytes.length);
    	byte[] valueLengthBytes = Bytes.toBytes(value.array().length);
    	byte[] oldVersionBytes = Bytes.toBytes(oldVersion);
    	
    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
    	try {
			outputStream.write( keyLengthBytes);
	    	outputStream.write( keyBytes );
	    	outputStream.write( valueLengthBytes );
	    	outputStream.write( value.array() );
	    	outputStream.write( oldVersionBytes );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	byte[] concatenated = outputStream.toByteArray( );
    	serialized = ByteBuffer.wrap(concatenated);
    	return serialized;    
    }
    
    //overload constructor
    public Option(ByteBuffer serialized){
    	byte[] concatenated = serialized.array();
    	
    	//parse the keyLength
    	byte[] keyLengthBytes = new byte[32];
    	for (int i= 0; i < 32; i++){
    		keyLengthBytes[i] = concatenated[i];
    	}
    	int keyLength = Bytes.toInt(keyLengthBytes);
    	
    	//parse the key
    	byte[] keyBytes = new byte[keyLength];
    	for (int i= 0; i < keyLength; i++){
    		keyBytes[i] = concatenated[32 + i];
    	}
    	String key = Bytes.toString(keyBytes);
    	
    	//parse the valueLength
    	byte[] valueLengthBytes = new byte[32];
    	for (int i= 0; i < 32; i++){
    		valueLengthBytes[i] = concatenated[32 + keyLength + i];
    	}
    	int valueLength = Bytes.toInt(valueLengthBytes);
    	
    	//parse the value
    	byte[] valueBytes = new byte[valueLength];
    	for (int i= 0; i < valueLength; i++){
    		valueBytes[i] = concatenated[32 + keyLength + 32 + i];
    	}
    	ByteBuffer value = ByteBuffer.wrap(valueBytes);
    	
    	//parse the oldVersion
    	byte[] oldVersionBytes = new byte[64];
    	for (int i= 0; i < 32; i++){
    		oldVersionBytes[i] = concatenated[32 + keyLength + 32 + valueLength + i];
    	}
    	long oldVersion = Bytes.toLong(oldVersionBytes);
    	
    	this.key = key;
        this.value = value;
        this.oldVersion = oldVersion;
        this.classic = false;
    }
}
