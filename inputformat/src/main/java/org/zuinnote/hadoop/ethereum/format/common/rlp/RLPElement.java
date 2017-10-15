/**
* Copyright 2017 ZuInnoTe (Jörn Franke) <zuinnote@gmail.com>
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
package org.zuinnote.hadoop.ethereum.format.common.rlp;


public class RLPElement implements RLPObject {
	
	private byte[] rawData;
	private byte[] indicator;
	
	public RLPElement(byte[] indicator, byte[] rawData) {
		this.indicator=indicator;
		this.rawData=rawData;
	}
	
	public byte[] getRawData() {
		return this.rawData;
	}
	
	public byte[] getIndicator() {
		return this.indicator;
	}

}
