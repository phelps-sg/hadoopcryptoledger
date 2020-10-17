/**
* Copyright 2016 ZuInnoTe (Jörn Franke) <zuinnote@gmail.com>
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
**/

package org.zuinnote.hadoop.bitcoin.format.common;


import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BitcoinUtilTest {

  @Test
  public void convertSignedIntToUnsigned() {
    long unsignedint = BitcoinUtil.convertSignedIntToUnsigned(-1);
    assertEquals( 4294967295L,unsignedint,"-1 from signed int must be 4294967295L unsigned");
  }

  @Test
  public void convertIntToByteArray() {
    byte[] intByteArray = BitcoinUtil.convertIntToByteArray(1);
    byte[] comparatorArray = new byte[]{0x00,0x00,0x00,0x01};
    assertArrayEquals( comparatorArray,intByteArray,"1 in int must be equivalent to the array {0x00,0x00,0x00,0x01}");
  }

  @Test
  public void convertLongToByteArray() {
    byte[] longByteArray = BitcoinUtil.convertLongToByteArray(1L);
    byte[] comparatorArray = new byte[]{0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x01};
    assertArrayEquals( comparatorArray,longByteArray,"1 in int must be equivalent to the array {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x01}");
  }

  @Test
  public void convertVarIntByteBufferToByteArray() {
    //note we will test here only one possible var int, because convertVarIntByteBufferToByteArray calls BitcoinUtil.getVarIntSize internally and thus we will do more tests when testing this function
    byte[] originVarInt = new byte[]{0x01};
    ByteBuffer testByteBuffer = ByteBuffer.allocate(1).put(originVarInt);
    testByteBuffer.flip();
    byte[] varIntByteArray = BitcoinUtil.convertVarIntByteBufferToByteArray(testByteBuffer);
    assertArrayEquals( originVarInt,varIntByteArray,"0x01 in ByteBuffer must be equivalent to the varint represented in array {0x01}");
  }

  @Test
  public void convertVarIntByteBufferToLong() {
    // note we will test here only one possible varint, because convertVarIntByteBufferToLong calls BitcoinUtil.getVarInt internally and thus we will do more tests when testing this function
   ByteBuffer testByteBuffer = ByteBuffer.allocate(1).put(new byte[]{0x01});
    testByteBuffer.flip();
    long convertedLong = BitcoinUtil.convertVarIntByteBufferToLong(testByteBuffer);
    assertEquals(1L, convertedLong,"0x01 in ByteBuffer must be 1 as a long");
  }



  @Test
  public void getVarIntByte() {
    byte[] originalVarInt = new byte[] {0x01};
    long convertedVarInt = BitcoinUtil.getVarInt(originalVarInt);
    assertEquals( 1L,convertedVarInt,"varInt {0x01} must be 1 as long");
  }

  @Test
  public void getVarIntWord() {
    byte[] originalVarInt = new byte[] {(byte)0xFD,0x01,0x00};
    long convertedVarInt = BitcoinUtil.getVarInt(originalVarInt);
    assertEquals( 1L, convertedVarInt,"varInt {0xFD,0x01,0x00} must be 1 as long");
  }

  @Test
  public void getVarIntDWord() {
    byte[] originalVarInt = new byte[] {(byte)0xFE,0x01,0x00,0x00,0x00};
    long convertedVarInt = BitcoinUtil.getVarInt(originalVarInt);
    assertEquals( 1L, convertedVarInt,"varInt {0xFE,0x01,0x00,0x00,0x00} must be 1 as long");
  }

  @Test
  public void getVarIntQWord() {
    byte[] originalVarInt = new byte[] {(byte)0xFF,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
    long convertedVarInt = BitcoinUtil.getVarInt(originalVarInt);
    assertEquals( 1L, convertedVarInt,"varInt {0xFF,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00} must be 1 as long");
  }


  @Test
  public void getVarIntSizeByte() {
    byte[] originalVarInt = new byte[] {0x02};
    byte varIntSize = BitcoinUtil.getVarIntSize(originalVarInt[0]);
    assertEquals( 1, varIntSize,"varInt {0x02} must be of size 1");
  }

  @Test
  public void getVarIntSizeWord() {
    byte[] originalVarInt = new byte[] {(byte)0xFD,0x01,0x00};
    byte varIntSize = BitcoinUtil.getVarIntSize(originalVarInt[0]);
    assertEquals( 3, varIntSize,"varInt {0xFD,0x01,0x00} must be of size 3");
  }


  @Test
  public void getVarIntSizeDWord() {
    byte[] originalVarInt = new byte[] {(byte)0xFE,0x01,0x00,0x00,0x00};
    byte varIntSize = BitcoinUtil.getVarIntSize(originalVarInt[0]);
    assertEquals( 5, varIntSize,"varInt {0xFE,0x01,0x00,0x00,0x00} must be of size 5");
  }


  @Test
  public void getVarIntSizeQWord() {
    byte[] originalVarInt = new byte[] {(byte)0xFF,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
    byte varIntSize = BitcoinUtil.getVarIntSize(originalVarInt[0]);
    assertEquals( 9, varIntSize,"varInt {0xFF,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00} must be of size 9");
  }


  @Test
  public void getSize() {
    byte[] blockSize = new byte[] {(byte)0x1D,0x01,0x00,0x00}; // this is the size of the genesis block
    long blockSizeLong = BitcoinUtil.getSize(blockSize);
    assertEquals( 285, blockSizeLong,"Size in Array {0x1D,0x01,0x00,0x00} must be 285");
  }


  @Test
  public void reverseByteArray() {
    byte[] originalByteArray = new byte[]{0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08};
    byte[] resultByteArray = BitcoinUtil.reverseByteArray(originalByteArray);
    byte[] reverseByteArray = new byte[]{0x08,0x07,0x06,0x05,0x04,0x03,0x02,0x01};
    assertArrayEquals( reverseByteArray, resultByteArray,"{0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08} is equivalent to {0x08,0x07,0x06,0x05,0x04,0x03,0x02,0x01} in reverse order");
  }


  @Test
  public void convertHexStringToByteArray() {
    String hexString = "01FF02";
    byte[] resultArray = BitcoinUtil.convertHexStringToByteArray(hexString);
    byte[] expectedByteArray = new byte[]{0x01,(byte)0xFF,0x02};
    assertArrayEquals( expectedByteArray, resultArray,"String \""+hexString+"\" is equivalent to byte array {0x01,0xFF,0x02}");
  }

  @Test
  public void convertByteArrayToHexString() {
    byte[] hexByteArray = new byte[]{0x01,(byte)0xFF,0x02};
    String resultString = BitcoinUtil.convertByteArrayToHexString(hexByteArray);
    String expectedString = "01FF02";
    assertEquals( expectedString, resultString,"Byte array {0x01,0xFF,0x02} is equivalent to Hex String \""+expectedString+"\"");
  }


  @Test
  public void convertIntToDate() {
    int genesisBlockTimeStamp=1231006505;
    Date genesisBlockDate = BitcoinUtil.convertIntToDate(genesisBlockTimeStamp);
    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
    String genesisBlockDateString = simpleFormat.format(genesisBlockDate);
    String expectedDate="2009-01-03";
    assertEquals( expectedDate, genesisBlockDateString,"1231006505 is equivalent to the Date 2009-01-03");
  }


  @Test
  public void compareMagicsPos() {
    byte[] inputMagic1 = new byte[]{(byte)0xF9,(byte)0xBE,(byte)0xB4,(byte)0xD9};
    byte[] inputMagic2 = new byte[]{(byte)0xF9,(byte)0xBE,(byte)0xB4,(byte)0xD9};
    boolean isSame = BitcoinUtil.compareMagics(inputMagic1,inputMagic2);
    assertTrue( isSame,"Magic 1 {0xF9,0xBE,0xB4,0xD9} is equivalent to Magic 2 {0xF9,0xBE,0xB4,0xD9}");
  }


  @Test
  public void compareMagicsNeg() {
    byte[] inputMagic1 = new byte[]{(byte)0xF9,(byte)0xBE,(byte)0xB4,(byte)0xD9};
    byte[] inputMagic2 = new byte[]{(byte)0xFA,(byte)0xBF,(byte)0xB5,(byte)0xDA};
    boolean isSame = BitcoinUtil.compareMagics(inputMagic1,inputMagic2);
    assertFalse( isSame,"Magic 1 {0xF9,0xBE,0xB4,0xD9} is not equivalent to Magic 2 {0xFA,0xBF,0xB5,0xDA}");
  }



  @Test
  public void getTransactionHash() throws NoSuchAlgorithmException, IOException {
	 // reconstruct the transaction from the genesis block
	int version=1;
	byte[] inCounter = new byte[]{0x01};
	byte[] previousTransactionHash = new byte[]{0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
	long previousTxOutIndex = 4294967295L;
	byte[] txInScriptLength = new byte[]{(byte)0x4D};
	byte[] txInScript= new byte[]{(byte)0x04,(byte)0xFF,(byte)0xFF,(byte)0x00,(byte)0x1D,(byte)0x01,(byte)0x04,(byte)0x45,(byte)0x54,(byte)0x68,(byte)0x65,(byte)0x20,(byte)0x54,(byte)0x69,(byte)0x6D,(byte)0x65,(byte)0x73,(byte)0x20,(byte)0x30,(byte)0x33,(byte)0x2F,(byte)0x4A,(byte)0x61,(byte)0x6E,(byte)0x2F,(byte)0x32,(byte)0x30,(byte)0x30,(byte)0x39,(byte)0x20,(byte)0x43,(byte)0x68,(byte)0x61,(byte)0x6E,(byte)0x63,(byte)0x65,(byte)0x6C,(byte)0x6C,(byte)0x6F,(byte)0x72,(byte)0x20,(byte)0x6F,(byte)0x6E,(byte)0x20,(byte)0x62,(byte)0x72,(byte)0x69,(byte)0x6E,(byte)0x6B,(byte)0x20,(byte)0x6F,(byte)0x66,(byte)0x20,(byte)0x73,(byte)0x65,(byte)0x63,(byte)0x6F,(byte)0x6E,(byte)0x64,(byte)0x20,(byte)0x62,(byte)0x61,(byte)0x69,(byte)0x6C,(byte)0x6F,(byte)0x75,(byte)0x74,(byte)0x20,(byte)0x66,(byte)0x6F,(byte)0x72,(byte)0x20,(byte)0x62,(byte)0x61,(byte)0x6E,(byte)0x6B,(byte)0x73};
	long seqNo=4294967295L;
	byte[] outCounter = new byte[]{0x01};
	long value=5000000000L;
	byte[] txOutScriptLength=new byte[]{(byte)0x43};
	byte[] txOutScript=new byte[]{(byte)0x41,(byte)0x04,(byte)0x67,(byte)0x8A,(byte)0xFD,(byte)0xB0,(byte)0xFE,(byte)0x55,(byte)0x48,(byte)0x27,(byte)0x19,(byte)0x67,(byte)0xF1,(byte)0xA6,(byte)0x71,(byte)0x30,(byte)0xB7,(byte)0x10,(byte)0x5C,(byte)0xD6,(byte)0xA8,(byte)0x28,(byte)0xE0,(byte)0x39,(byte)0x09,(byte)0xA6,(byte)0x79,(byte)0x62,(byte)0xE0,(byte)0xEA,(byte)0x1F,(byte)0x61,(byte)0xDE,(byte)0xB6,(byte)0x49,(byte)0xF6,(byte)0xBC,(byte)0x3F,(byte)0x4C,(byte)0xEF,(byte)0x38,(byte)0xC4,(byte)0xF3,(byte)0x55,(byte)0x04,(byte)0xE5,(byte)0x1E,(byte)0xC1,(byte)0x12,(byte)0xDE,(byte)0x5C,(byte)0x38,(byte)0x4D,(byte)0xF7,(byte)0xBA,(byte)0x0B,(byte)0x8D,(byte)0x57,(byte)0x8A,(byte)0x4C,(byte)0x70,(byte)0x2B,(byte)0x6B,(byte)0xF1,(byte)0x1D,(byte)0x5F,(byte)0xAC};
	int lockTime = 0;
	List<BitcoinTransactionInput> genesisInput = new ArrayList<BitcoinTransactionInput>(1);
	genesisInput.add(new BitcoinTransactionInput(previousTransactionHash,previousTxOutIndex,txInScriptLength,txInScript,seqNo));
	List<BitcoinTransactionOutput> genesisOutput = new ArrayList<BitcoinTransactionOutput>(1);
	genesisOutput.add(new BitcoinTransactionOutput(BigInteger.valueOf(value),txOutScriptLength,txOutScript));
	 BitcoinTransaction genesisTransaction = new BitcoinTransaction(version,inCounter,genesisInput,outCounter,genesisOutput,lockTime);
	byte[] genesisTransactionHash=BitcoinUtil.getTransactionHash(genesisTransaction);
	 byte[] expectedHash = BitcoinUtil.reverseByteArray(new byte[]{(byte)0x4A,(byte)0x5E,(byte)0x1E,(byte)0x4B,(byte)0xAA,(byte)0xB8,(byte)0x9F,(byte)0x3A,(byte)0x32,(byte)0x51,(byte)0x8A,(byte)0x88,(byte)0xC3,(byte)0x1B,(byte)0xC8,(byte)0x7F,(byte)0x61,(byte)0x8F,(byte)0x76,(byte)0x67,(byte)0x3E,(byte)0x2C,(byte)0xC7,(byte)0x7A,(byte)0xB2,(byte)0x12,(byte)0x7B,(byte)0x7A,(byte)0xFD,(byte)0xED,(byte)0xA3,(byte)0x3B});
	 assertArrayEquals( expectedHash, genesisTransactionHash,"Hash for Genesis Transaction correctly calculated");
  }

  @Test
  public void getTransactionHashSegWit() throws NoSuchAlgorithmException, IOException {
	 // reconstruct the transaction from the a random segwit block
	int version=2;
	byte marker=0x00;
	byte flag=0x01;
	byte[] inCounter = new byte[]{0x01};
	byte[] previousTransactionHash = new byte[]{(byte)0x07,(byte)0x21,(byte)0x35,(byte)0x23,(byte)0x6D,(byte)0x2E,(byte)0xBC,(byte)0x78,(byte)0xB6,(byte)0xAC,(byte)0xE1,(byte)0x88,(byte)0x97,(byte)0x03,(byte)0xB1,(byte)0x84,(byte)0x85,(byte)0x52,(byte)0x87,(byte)0x12,(byte)0xBD,(byte)0x70,(byte)0xE0,(byte)0x7F,(byte)0x4A,(byte)0x90,(byte)0x11,(byte)0x40,(byte)0xDE,(byte)0x38,(byte)0xA2,(byte)0xE8};
	long previousTxOutIndex = 1L;

	byte[] txInScriptLength = new byte[]{(byte)0x17};
	
	byte[] txInScript= new byte[]{(byte)0x16,(byte)0x00,(byte)0x14,(byte)0x4D,(byte)0x4D,(byte)0x83,(byte)0xED,(byte)0x5F,(byte)0x10,(byte)0x7B,(byte)0x8D,(byte)0x45,(byte)0x1E,(byte)0x59,(byte)0xA0,(byte)0x43,(byte)0x1A,(byte)0x13,(byte)0x92,(byte)0x79,(byte)0x6B,(byte)0x26,(byte)0x04};
	long seqNo=4294967295L;
	byte[] outCounter = new byte[]{0x02};
	long value_1=1009051983L;
	byte[] txOutScriptLength_1=new byte[]{(byte)0x17};
	byte[] txOutScript_1=new byte[]{(byte)0xA9,(byte)0x14,(byte)0xF0,(byte)0x50,(byte)0xC5,(byte)0x91,(byte)0xEA,(byte)0x98,(byte)0x26,(byte)0x73,(byte)0xCC,(byte)0xED,(byte)0xF5,(byte)0x21,(byte)0x13,(byte)0x65,(byte)0x7B,(byte)0x67,(byte)0x83,(byte)0x03,(byte)0xE6,(byte)0xA1,(byte)0x87};
	long value_2=59801109L;
	byte[] txOutScriptLength_2=new byte[]{(byte)0x19};
	byte[] txOutScript_2=new byte[]{(byte)0x76,(byte)0xA9,(byte)0x14,(byte)0xFB,(byte)0x2E,(byte)0x13,(byte)0x83,(byte)0x5E,(byte)0x39,(byte)0x88,(byte)0xC7,(byte)0x8F,(byte)0x76,(byte)0x0D,(byte)0x4A,(byte)0xC8,(byte)0x1E,(byte)0x04,(byte)0xEA,(byte)0xF1,(byte)0x94,(byte)0xEA,(byte)0x92,(byte)0x88,(byte)0xAC};
	
	// there is only one input so we have only one list of stack items containing 2 items in this case
	byte[] noOfStackItems = new byte[]{0x02};
	byte[] segwitnessLength_1=new byte[]{(byte)0x48};
	byte[] segwitnessScript_1 = new byte[]{(byte)0x30,(byte)0x45,(byte)0x02,(byte)0x21,(byte)0x00,(byte)0xBB,(byte)0x5F,(byte)0x78,(byte)0xE8,(byte)0xA1,(byte)0xBA,(byte)0x5E,(byte)0x14,(byte)0x26,(byte)0x1B,(byte)0x0A,(byte)0xD3,(byte)0x95,(byte)0x56,(byte)0xAF,(byte)0x9B,(byte)0x21,(byte)0xD9,(byte)0x1F,(byte)0x67,(byte)0x5D,(byte)0x38,(byte)0xC8,(byte)0xCD,(byte)0xAD,(byte)0x7E,(byte)0x7F,(byte)0x5D,(byte)0x21,(byte)0x00,(byte)0x4A,(byte)0xBD,(byte)0x02,(byte)0x20,(byte)0x4C,(byte)0x1E,(byte)0xAC,(byte)0xF1,(byte)0xF9,(byte)0xAC,(byte)0x1D,(byte)0xCC,(byte)0x61,(byte)0x63,(byte)0xF2,(byte)0x07,(byte)0xFC,(byte)0xBC,(byte)0x49,(byte)0x8B,(byte)0x32,(byte)0x4C,(byte)0xBE,(byte)0xF5,(byte)0x7F,(byte)0x83,(byte)0x9F,(byte)0xA2,(byte)0xC2,(byte)0x55,(byte)0x57,(byte)0x4B,(byte)0x2F,(byte)0x37,(byte)0x19,(byte)0xBC,(byte)0x01};
	byte[] segwitnessLength_2=new byte[]{(byte)0x21};
	byte[] segwitnessScript_2 = new byte[]{(byte)0x03,(byte)0xC5,(byte)0x3F,(byte)0xEA,(byte)0x9A,(byte)0xE5,(byte)0x61,(byte)0xB6,(byte)0x05,(byte)0x74,(byte)0xB2,(byte)0xD5,(byte)0x10,(byte)0x27,(byte)0x3F,(byte)0x7C,(byte)0x51,(byte)0x60,(byte)0x69,(byte)0x7E,(byte)0xB4,(byte)0x7B,(byte)0x48,(byte)0x8E,(byte)0x95,(byte)0xAD,(byte)0x62,(byte)0x91,(byte)0xBB,(byte)0xCB,(byte)0x5E,(byte)0x43,(byte)0xA2};
	int lockTime = 0;
	List<BitcoinTransactionInput> randomScriptWitnessInput = new ArrayList<BitcoinTransactionInput>(1);
	randomScriptWitnessInput.add(new BitcoinTransactionInput(previousTransactionHash,previousTxOutIndex,txInScriptLength,txInScript,seqNo));
	List<BitcoinTransactionOutput> randomScriptWitnessOutput = new ArrayList<BitcoinTransactionOutput>(2);
	
	randomScriptWitnessOutput.add(new BitcoinTransactionOutput(BigInteger.valueOf(value_1),txOutScriptLength_1,txOutScript_1));
	randomScriptWitnessOutput.add(new BitcoinTransactionOutput(BigInteger.valueOf(value_2),txOutScriptLength_2,txOutScript_2));
	List<BitcoinScriptWitnessItem> randomScriptWitnessSWI = new ArrayList<BitcoinScriptWitnessItem>(1);
	List<BitcoinScriptWitness> randomScriptWitnessSW = new ArrayList<BitcoinScriptWitness>(2);
	randomScriptWitnessSW.add(new BitcoinScriptWitness(segwitnessLength_1,segwitnessScript_1));
	randomScriptWitnessSW.add(new BitcoinScriptWitness(segwitnessLength_2,segwitnessScript_2));
	randomScriptWitnessSWI.add(new BitcoinScriptWitnessItem(noOfStackItems,randomScriptWitnessSW));
	 BitcoinTransaction randomScriptWitnessTransaction = new BitcoinTransaction(marker,flag,version,inCounter,randomScriptWitnessInput,outCounter,randomScriptWitnessOutput,randomScriptWitnessSWI,lockTime);
	byte[] randomScriptWitnessTransactionHash=BitcoinUtil.getTransactionHash(randomScriptWitnessTransaction);
	 byte[] expectedHash = BitcoinUtil.reverseByteArray(new byte[]{(byte)0x47,(byte)0x52,(byte)0x1C,(byte)0x2A,(byte)0x13,(byte)0x45,(byte)0x5E,(byte)0x92,(byte)0xD3,(byte)0xBD,(byte)0x56,(byte)0x3F,(byte)0xAD,(byte)0xA5,(byte)0x78,(byte)0x6E,(byte)0x85,(byte)0xB4,(byte)0x5E,(byte)0x96,(byte)0x85,(byte)0xA8,(byte)0xC9,(byte)0xA3,(byte)0xFE,(byte)0xB8,(byte)0x9A,(byte)0x4F,(byte)0xB5,(byte)0x0D,(byte)0xAF,(byte)0xF5});
	
	 assertArrayEquals( expectedHash, randomScriptWitnessTransactionHash,"Hash for Random ScriptWitness Transaction correctly calculated (txId)");
	 byte[] randomScriptWitnessTransactionHashSegWit=BitcoinUtil.getTransactionHashSegwit(randomScriptWitnessTransaction);
	 
	 //74700E2CE030013E2E10FCFD06DF99C7826E41C725CA5C467660BFA4874F65BF
	 byte[] expectedHashSegwit = BitcoinUtil.reverseByteArray(new byte[]{(byte)0x74,(byte)0x70,(byte)0x0E,(byte)0x2C,(byte)0xE0,(byte)0x30,(byte)0x01,(byte)0x3E,(byte)0x2E,(byte)0x10,(byte)0xFC,(byte)0xFD,(byte)0x06,(byte)0xDF,(byte)0x99,(byte)0xC7,(byte)0x82,(byte)0x6E,(byte)0x41,(byte)0xC7,(byte)0x25,(byte)0xCA,(byte)0x5C,(byte)0x46,(byte)0x76,(byte)0x60,(byte)0xBF,(byte)0xA4,(byte)0x87,(byte)0x4F,(byte)0x65,(byte)0xBF});
	 assertArrayEquals( expectedHashSegwit, randomScriptWitnessTransactionHashSegWit,"Hash for Random ScriptWitness Transaction correctly calculated (wtxId)");

	 
  }


} 


