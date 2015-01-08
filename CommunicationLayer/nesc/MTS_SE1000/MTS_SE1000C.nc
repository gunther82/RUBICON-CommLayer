/*****************************************************************************************
 * Copyright (c) 2000-2003 The Regents of the University of California.  
 * All rights reserved.
 * Copyright (c) 2005 Arch Rock Corporation
 * All rights reserved.
 * Copyright (c) 2006, Technische Universitaet Berlin
 * All rights reserved.
 * Copyright (c) 2010, ADVANTIC Sistemas y Servicios S.L.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 *
 *    * Redistributions of source code must retain the above copyright notice, this list  
 * of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright notice, this  
 * list of conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution.
 *    * Neither the name of ADVANTIC Sistemas y Servicios S.L. nor the names of its 
 * contributors may be used to endorse or promote products derived from this software 
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * - Revision -------------------------------------------------------------
 * $Revision: 1.0 $
 * $Date: 2010/12/12 18:24:06 $
 * @author: Manuel Fernandez <manuel.fernandez@advanticsys.com>
*****************************************************************************************/
generic configuration MTS_SE1000C() 
{
   provides 
   {
   //TODO: Add GPIO input for buzzer
   
  	interface DeviceMetadata;
				
  	interface Read<uint16_t> as PIR_LHI878;
	interface Read<uint16_t> as Magnetic_AMS_10C;
	interface Read<uint16_t> as Mic_SPOB_413S44;
  
  	interface ReadStream<uint16_t> as PIR_LHI878_Stream;
	interface ReadStream<uint16_t> as Magnetic_AMS_10C_Stream;
	interface ReadStream<uint16_t> as Mic_SPOB_413S44_Stream;


   }
}
implementation {
        //--Read--
	components new AdcReadClientC() as AdcRead_PIR_LHI878;
	components new AdcReadClientC() as AdcRead_Magnetic_AMS_10C;
	components new AdcReadClientC() as AdcRead_Mic_SPOB_413S44;

  
	PIR_LHI878        = AdcRead_PIR_LHI878;
	Magnetic_AMS_10C  = AdcRead_Magnetic_AMS_10C;
	Mic_SPOB_413S44   = AdcRead_Mic_SPOB_413S44;


	AdcRead_PIR_LHI878.AdcConfigure       -> MTS_SE1000P.PIR_LHI878;
	AdcRead_Magnetic_AMS_10C.AdcConfigure -> MTS_SE1000P.Magnetic_AMS_10C;
	AdcRead_Mic_SPOB_413S44.AdcConfigure  -> MTS_SE1000P.Mic_SPOB_413S44;
	
	
	//--Read Stream--
	components new AdcReadStreamClientC() as AdcReadStream_PIR_LHI878;
	components new AdcReadStreamClientC() as AdcReadStream_Magnetic_AMS_10C;
	components new AdcReadStreamClientC() as AdcReadStream_Mic_SPOB_413S44;

  
	PIR_LHI878_Stream       = AdcReadStream_PIR_LHI878;
	Magnetic_AMS_10C_Stream = AdcReadStream_Magnetic_AMS_10C;
	Mic_SPOB_413S44_Stream  = AdcReadStream_Mic_SPOB_413S44;

  
        AdcReadStream_PIR_LHI878.AdcConfigure       -> MTS_SE1000P.PIR_LHI878;
	AdcReadStream_Magnetic_AMS_10C.AdcConfigure -> MTS_SE1000P.Magnetic_AMS_10C;
	AdcReadStream_Mic_SPOB_413S44.AdcConfigure  -> MTS_SE1000P.Mic_SPOB_413S44;


	components       MTS_SE1000P;
        DeviceMetadata = MTS_SE1000P;
}
