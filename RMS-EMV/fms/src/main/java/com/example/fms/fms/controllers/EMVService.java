package com.example.fms.fms.controllers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

import com.example.fms.fms.models.OTCEMVPaymentReq;
import com.fazecast.jSerialComm.SerialPort;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EMVService {
    
    private static final Logger logger = Logger.getLogger(EMVService.class.getName());
    
	// private final OTCCollectionReceiptingRepository otcCollectionReceiptingRepository;

	// @Autowired
    // private AuthService authService;

    // @Autowired
    // private OTCCollectionReceiptingService spService;

	// @Autowired
    // private EmailService emailService;

    // @Autowired
    // private OTCReceiptGenerator receiptGenerator;

    // @Autowired
    // private IdamanAPIUploadService idamanAPIUploadService;

	//Machine info
	// private static Integer gi_baudRate = 9600;
	// private static Integer gs_databits = 8;
	// private static int gi_parity = SerialPort.NO_PARITY;
	// private static int gi_stopbits = SerialPort.ONE_STOP_BIT;

    private static boolean CarPark = true;
	private static int sleep = 5000;
	// private static String portName = "COM3";
	private static String portName = SerialPortFinder.getCP210xPort();

	// Sales
	// private static String command = "C200";
	private static String hostNo = "CP";

	// Void
	// private static String command = "C201";
	// private static String hostNo = "03";
	private static String TraceNo = "000188";

	// Settlement
	// private static String command = "C500";
	// private static String hostNo = "03";

	// Query
	// private static String command = "C208";

	private static String command = "";

	private static String uid = "TI0000000000000000000001";
	private static String duplicateReceipt = "0";

	private static String amount = "";
	private static String additionalData = "TIOR20240409000001";
	// private static String additionalData = "TI0000000000000000000001";

	// public EMVService(OTCCollectionReceiptingRepository otcCollectionReceiptingRepository){
	// 	this.otcCollectionReceiptingRepository = otcCollectionReceiptingRepository;
	// }

    public EMVService() {
    }

	public void setAmount(String amount) {
        EMVService.amount = amount;
    }

	public void setAdditionalData(String additionalData) {
		EMVService.additionalData = additionalData;
		log("Additional Data: " + additionalData);
	}

	public void setCommand(String command) {
		EMVService.command = command;
		log("Command: " + command);
	}

    public String emvUSB(OTCEMVPaymentReq insertRequest) {
		// Set up logger
		setupLogger();
        SerialPort[] ports = SerialPort.getCommPorts();
        System.out.println("Available Serial Ports:");

		if(ports.length > 0){
			for (SerialPort port : ports)
			{
				System.out.println(port.getSystemPortName());
				
				if(port.getSystemPortName().equals(portName))
				// if(port.getSystemPortName().equals(SerialPortFinder.getCP210xPort()))
				{
					SerialPort serialPort = SerialPort.getCommPort(portName); // Change to your actual port
					// SerialPort serialPort = SerialPort.getCommPort(SerialPortFinder.getCP210xPort()); // Change to your actual port
					serialPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
					serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED); // Add or adjust flow control if required
					serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 15000, 15000);
	
					if(!serialPort.openPort())
					{
						serialPort.openPort();
					}
	
					// Start Initiate Txn send ENQ
					byte[] enqCommand = {0x05};
					CarPark = enquiry(enqCommand,serialPort);
					// Send Txn Command (C200 - Sale)
					if(CarPark)
					{
						CarPark = writeToCOM(hostNo ,command , serialPort);
						if(CarPark)
						{
							String result = readFromCOM(serialPort, insertRequest);
                            return result;
						}
						else 
						{
							byte[] enqCommandFail = {0x04};
							enquiry(enqCommandFail,serialPort);
							log("Fail write response, sending 0X04");
							log("No response received after transaction.");
							serialPort.closePort();
						}
					}
					else 
					{
						byte[] enqCommandFail = {0x04};
						enquiry(enqCommandFail,serialPort);
						log("Fail enq response, sending 0X04");
						log("No response received after enquiry.");
						serialPort.closePort();
					}					
				}
				else
				{
					log("ENQ:- No PORT found!");
				}
			}
		}else
		{
			log("ENQ:- No PORT found!");
		}
        return "";
    }

    // Placeholder for enquiry function
    public static boolean enquiry(byte[] enqCommand, SerialPort port) 
    {
        // Simulate enquiry
        boolean lb_success = false;
        int li_retry = 0;
        int li_response = 0;
        int statusCode = 0;
        String statusRemark = "";

		while(li_retry < 4)
		{
			li_retry += 1;
			log("ENQ:-Retry: " + li_retry);

			try{
				//if third attempt send EOT;
				if(li_retry == 4)
				{
					statusCode = 1;
					statusRemark = "ENQ failed";
					port.getOutputStream().write(enqCommand);
					port.getOutputStream().flush();
					Thread.sleep(sleep);
					break;
				}

				//if timeout send EOT
				if (statusCode == 1)
				{
					port.getOutputStream().write(enqCommand);
					port.getOutputStream().flush();
					log("ENQ:-Write <EOT>");
				}

				//set write timeout
				port.getOutputStream().write(enqCommand);
				port.getOutputStream().flush();

				Thread.sleep(sleep);
				li_response = port.getInputStream().read();
				log("ENQ:-Write: " + li_response);

				//Possitive ACK
				if (li_response == 6)
				{
					li_retry = 3;
					lb_success = true;
					statusCode = 0;
					statusRemark = "ENQ Success";
					log("ENQ:-Read <ACK> " + statusRemark);
					break;
				}//Negative ACK
				else if (li_response == 21)
				{
					statusCode = 1;
					statusRemark = "ENQ failed";
					log("ENQ:-Read <NAK> " + statusRemark);
					Thread.sleep(sleep);
					//break;
				}//Negative Response
				else{
					log("ENQ:-Read <Other> " + li_response);
					Thread.sleep(sleep);
				}
			}catch(Exception ex){
				statusCode = 1;
				statusRemark = ex.getMessage();
				lb_success = false;
				log("Enquiry: " + ex.getMessage());
			}
		}

		return lb_success;
    }

    // Placeholder for WriteToCOM function
	public static boolean writeToCOM(String hostNo, String command, SerialPort serialPort) 
	{
		//log("Writing to : " + serialPort.getSystemPortName());

        boolean success = false;
		String packet = "";

		if(command.equals("C200"))
		{
			// Convert amount to required format (12 digits, left-padded with zeros)
			String formattedAmount = String.format("%012d", (int) (Double.parseDouble(amount) * 100));  // Example: 100.00 becomes 00000010000
				
			// Ensure additional data is 24 characters long, padded with spaces
			String formattedAdditionalData = String.format("%-24s", additionalData);

			// Construct the full packet (STX + Data + ETX)
			packet = "\u0002" + command + hostNo + formattedAmount + formattedAdditionalData + "\u0003";
		}
		else if(command.equals("C201"))
		{
			// Convert amount to required format (12 digits, left-padded with zeros)
			String formattedAmount = String.format("%012d", (int) (Double.parseDouble(amount) * 100));  // Example: 100.00 becomes 00000010000
			
			// Ensure additional data is 24 characters long, padded with spaces
			String formattedTraceNo = String.format("%-6s", TraceNo);				

			// Construct the full packet (STX + Data + ETX)
			packet = "\u0002" + command + hostNo + formattedAmount + formattedTraceNo + "\u0003";
		}
		else if(command.equals("C500"))
		{
			// Construct the full packet (STX + Data + ETX)
			packet = "\u0002" + command + hostNo + "\u0003";
		}
		else if(command.equals("C208"))
		{
			// Convert amount to required format (12 digits, left-padded with zeros)
			String formattedAmount = String.format("%012d", (int) (Double.parseDouble(amount) * 100));  // Example: 100.00 becomes 00000010000
			log("additional data: " + additionalData);

			packet = "\u0002" + command  + additionalData + formattedAmount + duplicateReceipt +"\u0003";
		}
	 
		// Calculate LRC (Longitudinal Redundancy Check), excluding STX
		byte lrc = computeLRC(packet);
	 
		// Convert the packet to bytes (ASCII encoding) and append LRC byte
		byte[] byteData = (packet + (char) lrc).getBytes(StandardCharsets.US_ASCII);
	 
		// Print the packet in hexadecimal format before sending (for debugging)
		StringBuilder hexString = new StringBuilder();
		for (byte b : byteData) {
			hexString.append(String.format("%02X ", b));
		}
		//log("Sending in Hexadecimal: " + hexString.toString().trim());
	 
		// Open the serial port (Configure your serial port parameters)
		// SerialPort serialPort = SerialPort.getCommPort("COM5"); // Change to your actual port
		// serialPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
		// serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED); // Add or adjust flow control if required
		// serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 5000, 5000);
	 
		if (!serialPort.isOpen()) {
			log("Failed to open port.");
			return false;
		}
	 
		try {
			// Send an ENQ (Ping) to check if terminal is ready
			// byte[] enqCommand = {0x05};  // ENQ
			// OutputStream outputStream = serialPort.getOutputStream();
			// outputStream.write(enqCommand);
			// outputStream.flush();
			// System.out.println("Ping (ENQ) sent.");
	 
			// // Read response from terminal
			// byte[] pingResponse = new byte[1];
			//int numRead = serialPort.readBytes(pingResponse, pingResponse.length);
	 
			// ACK received
			//log("WriteToCOM:- Read <ACK> Write Success");
	
			// Send the packet (transaction)
			serialPort.getOutputStream().write(byteData);
			serialPort.getOutputStream().flush();

			log("WriteToCOM: -Write " + packet);
	
			Thread.sleep(sleep);

			// Read response from terminal
			byte[] readBuffer = new byte[10000];
			int numResponseBytes = serialPort.readBytes(readBuffer, 27, 0);
	
			if (numResponseBytes > 0) 
			{
				// Print the response in hexadecimal format
				StringBuilder responseHex = new StringBuilder();
				int exit = 0;
				while(exit == 0)
				{
					for (int i = 0; i < numResponseBytes; i++) {
						responseHex.append(String.format("%02X ", readBuffer[i]));
					}
					System.out.println("Response received (Hex): " + responseHex.toString().trim());
	
					// Check if response is ACK (0x06) or NAK (0x15)
					if (readBuffer[0] == 0x06) {
						//log("ACK received after transaction.");
						//log("===============================");
						//System.out.println(numResponseBytes);
						log("WriteToCOM:- Read <ACK> Write Success");
						success = true;
						exit = 1;
						Thread.sleep(sleep);
					} else if (readBuffer[0] == 0x15) {
						//System.out.println("NAK received after transaction.");
						log("WriteToCOM:- <NAK> received after transaction.");
						Thread.sleep(sleep);
						success = false;
						exit = 1;
					}
					else {
						//System.out.println("Unexpected response." + serialPort.getInputStream().read());
						log("WriteToCOM:- <NAK> Unexpected response." + serialPort.getInputStream().read());
						success = false;
						exit = 1;
						Thread.sleep(sleep);
					}
				}
			} 
		} 
		catch (Exception e) {
			log(e.getMessage());
		} 

		return success;
	}
		
    // Placeholder for ReadFromCOM function
    public String readFromCOM(SerialPort serialPort, OTCEMVPaymentReq insertRequest) {
        // Simulate reading from COM port
        //System.out.println("Reading from " + serialPort.getSystemPortName());
		log("ReadFromCOM:- Read <ENQ>");

		int statusCode;
		int data;

		try{
			try{
				int li_eot = serialPort.getInputStream().read();
				//log("EOT: " + li_eot);
				int li_retry = 0;
				if(li_eot == 5){

					Boolean entry = true;
					byte[] readBuffer = new byte[4000];
					StringBuilder sb = new StringBuilder();

					//log("ReadFromCOM: Read <EOT> EOT Sucess");
					//Thread.sleep(sleep);
					//entry = enquiry(enqCommand, serialPort);

					if(entry)
					{
						while(li_retry < 3)
						{
							li_retry += 1;
							try{
								//statusCode = rcvData(readBuffer, 0, 1000, serialPort);
								statusCode = rcvData(serialPort);
								log("ReadFromCOM:- Write <ACK>");
								//log("Total byte: " + statusCode);
								if(statusCode > 4 && statusCode < 1000){
									data = serialPort.readBytes(readBuffer, statusCode, 0);

									for (int i = 0; i < data; i++) {
										sb.append(String.format("%02X", readBuffer[i]));  // %02X ensures 2-character uppercase hex
									}
								}

								String result = hexToString(sb.toString());
								log("ReadFromCOM:- Read " + result);
								
								System.out.println("==================================");
								System.out.println("             Result               ");
								System.out.println("==================================");

                                return result;

								// if(result.contains("R200")){

                                //     return "R200";
								// 	// displayC200(result, insertRequest);
								// }
								// else if(result.contains("R201"))
								// {
                                //     return "R201";
								// 	// displayC201(result);
								// }
								// else if(result.contains("R500"))
								// {
                                //     return "R500";
								// 	// displayC500(result);
								// }
								// else if(result.contains("R208"))
								// {
                                //     return "Query: " + result;
								// 	// System.out.println("Query: " + result);
								// }
								// else{
                                //     return "Others: " + result;
								// 	// System.out.println("Others: " + result);
								// }

								// break;
							}catch(Exception ex){
								log("ReadFromCOM: <ReceiveResponse> " + ex.getMessage());
							}
						}
					}
				}
			}catch(Exception ex){
				log("ReadFromCOM: <ABORT> " + ex.getMessage());
				String abort = "ABORT";
				byte[] abortArray = abort.getBytes(StandardCharsets.US_ASCII);
				serialPort.getOutputStream().write(abortArray);
				Thread.sleep(sleep);
			}
		}catch(Exception ex){
			log("Error" + ex.getMessage());
		}
		finally{
			serialPort.closePort();
			log("ReadFromCOM:- Read <EOT>");
			//System.out.println("Port closed.");
		}

        return "";
    }

	// Placeholder for LRC computation
	public static byte computeLRC(String packet) {
		byte lrc = 0;
		for (int i = 1; i < packet.length(); i++) {
			lrc ^= packet.charAt(i);
		}

		return lrc;
	}

	public static int rcvData(SerialPort serialPort)
	{
		//log("Receiving from port:" + serialPort);

		int len = 0;

		while(true)
		{
			if(len == serialPort.bytesAvailable() && len >= 4)
			{
				//log("Sub Entry: " + len);
				return len;
			}

			len = serialPort.bytesAvailable();
			//log("Main Entry: " + len);

			// Sleep for 1 second
			try {
				Thread.sleep(5000);  // 1000 milliseconds = 1 second
			} catch (InterruptedException e) {
				log("Sleep interrupted: " + e.getMessage());
				Thread.currentThread().interrupt();  // Restore interrupted status
			}
		}
	}

    public static String hexToString(String hex) 
	{
        // Convert the hex string to a byte array
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                 + Character.digit(hex.charAt(i+1), 16));
        }

        // Convert the byte array to a string
        return new String(bytes);
    }

/*
	public static String asciiOctets2String(byte[] bytes)
	{
		try
		{
			StringBuilder sb = new StringBuilder(bytes.length);
			String str = new String(bytes, StandardCharsets.UTF_8);
			for(char c : str.toCharArray())
			{
				switch (c)
				{
					case '\u0000': sb.append("<NUL>"); break;
					case '\u0001': sb.append("<SOH>"); break;
					case '\u0002': sb.append("<STX>"); break;
					case '\u0003': sb.append("<ETX>"); break;
					case '\u0004': sb.append("<EOT>"); break;
					case '\u0005': sb.append("<ENQ>"); break;
					case '\u0006': sb.append("<ACK>"); break;
					case '\u0007': sb.append("<BEL>"); break;
					case '\u0008': sb.append("<BS>"); break;
					case '\u0009': sb.append("<HT>"); break;

					case '\u000B': sb.append("<VT>"); break;
					case '\u000C': sb.append("<FF>"); break;

					case '\u000E': sb.append("<SO>"); break;
					case '\u000F': sb.append("<SI>"); break;
					case '\u0010': sb.append("<DLE>"); break;
					case '\u0011': sb.append("<DC1>"); break;
					case '\u0012': sb.append("<DC2>"); break;
					case '\u0013': sb.append("<DC3>"); break;
					case '\u0014': sb.append("<DC4>"); break;
					case '\u0015': sb.append("<NAK>"); break;
					case '\u0016': sb.append("<SYN>"); break;
					case '\u0017': sb.append("<ETB>"); break;
					case '\u0018': sb.append("<CAN>"); break;
					case '\u0019': sb.append("<EM>"); break;
					case '\u001A': sb.append("<SUB>"); break;
					case '\u001B': sb.append("<ESC>"); break;
					case '\u001C': sb.append("<FS>"); break;
					case '\u001D': sb.append("<GS>"); break;
					case '\u001E': sb.append("<RS>"); break;
					case '\u001F': sb.append("<US>"); break;
					case '\u007F': sb.append("<DEL>"); break;
					default:
						if (c > '\u007F')
						{
							// in ASCII, any octet in the range 0x80-0xFF doesn't have a character glyph associated with it
							sb.append("{0:X4}" + c);
						}
						else
						{
							sb.append(c);
						}
						break;
				}
			}

			return sb.toString();

		}
		catch (Exception e)
		{
			return "";
		}
	}
 */



	// public void displayC200(String transactionString, OTCEMVPaymentReq insertRequest) throws IOException{

	// 	System.out.println(transactionString);
    //     OTCEMVRequest emv = new OTCEMVRequest();

	// 	String response = transactionString.substring(3, 7);
    //     emv.setI_resp_cd(response);
    //     System.out.println("Response: " + response);

    //     String cardNumber = transactionString.substring(7, 26);
    //     emv.setI_card_no(cardNumber);
    //     System.out.println("Card Number: " + cardNumber);

    //     String expiryDate = transactionString.substring(26, 30);
    //     emv.setI_dt_expiry(expiryDate);
    //     System.out.println("Expiry Date: " + expiryDate);

    //     String statusCode = transactionString.substring(30, 32);
    //     emv.setI_status_cd(statusCode);
    //     System.out.println("Status Code: " + statusCode);

    //     String approvalCode = transactionString.substring(32, 37);
    //     emv.setI_approval_cd(approvalCode);
    //     System.out.println("Approval Code: " + approvalCode);

    //     String rrn = transactionString.substring(37, 50);
    //     emv.setI_rrn(rrn);
    //     System.out.println("RRN: " + rrn);

    //     String transactionTrace = transactionString.substring(50, 56);
    //     emv.setI_trans_trace(transactionTrace);
    //     System.out.println("Transaction Trace: " + transactionTrace);

    //     String batchNumber = transactionString.substring(56, 62);
    //     emv.setI_batch_no(batchNumber);
    //     System.out.println("Batch Number: " + batchNumber);

    //     String hostNo = transactionString.substring(62, 64);
    //     emv.setI_host_no(hostNo);
    //     System.out.println("Host No: " + hostNo);

    //     String terminalId = transactionString.substring(64, 72);
    //     emv.setI_t_id(terminalId);
    //     System.out.println("Terminal ID: " + terminalId);

    //     String merchantId = transactionString.substring(72, 87);
    //     emv.setI_mer_id(merchantId);
    //     System.out.println("Merchant ID: " + merchantId);

    //     String aid = transactionString.substring(87, 101);
    //     emv.setI_aid(aid);
    //     System.out.println("AID: " + aid);

    //     String tc = transactionString.substring(101, 117);  
    //     emv.setI_tc(tc);
    //     System.out.println("TC: " + tc);

	// 	String cardholderName = transactionString.substring(117, 142);  
    //     emv.setI_cardholder_nm(cardholderName);
    //     System.out.println("Cardholder Name: " + cardholderName);

	// 	String cardType = transactionString.substring(142, 145);  
    //     emv.setI_card_ty(cardType);
    //     System.out.println("Card Type: " + cardType);

    //     emv.setI_prtnr_txn_id(null);
    //     emv.setI_apay_txn_id(null);
    //     emv.setI_cust_id(null);
    //     emv.setI_amt(new BigDecimal(amount));
    //     emv.setI_add_data(additionalData);

    //     Integer result = spService.sp_insemvsale(emv);

	// 	insertRequest.setI_emv_sale(result);

	// 	Integer otc_id = otcCollectionReceiptingRepository.sp_insotcpymtemv(insertRequest);

	// 	Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

	// 	// Insert into rms_otc_rcpt table
	// 	OTCRcptRequest otcRcptRequest = new OTCRcptRequest();
	// 	otcRcptRequest.setI_otc_id(otc_id);
	// 	otcRcptRequest.setI_rcpt_no("TEST123");
	// 	otcRcptRequest.setI_rcpt_dt(timestamp);
	// 	otcRcptRequest.setI_rcpt_status("Valid");
	// 	otcRcptRequest.setI_rcpt_reprint(0);
	// 	otcRcptRequest.setI_is_uploaded(0);
	// 	otcRcptRequest.setI_ver_id("1");
	// 	otcRcptRequest.setI_ssdocref_id("");
	// 	otcRcptRequest.setI_created_by(authService.getLoginUserName());
	// 	otcRcptRequest.setI_created_by(authService.getLoginUserName());
	// 	otcRcptRequest.setI_file_nm("");
	// 	otcRcptRequest.setI_remark("");
		
	// 	OTCRcpt otcRcpt = spService.sp_insotcrcpt(otcRcptRequest);
	// 	OTCPaymentDone payment = spService.sp_getotcorderemv(insertRequest.getI_mtt_id());

	// 	// Use SimpleDateFormat to format the date
	// 	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	// 	String formattedDate = sdf.format(payment.getPayment_dt());
	// 	payment.setFormattedDate(formattedDate);

	// 	OTCHistReq collectionSlipSubmit = new OTCHistReq();
    //     collectionSlipSubmit.setI_mtt_id(insertRequest.getI_mtt_id());
    //     collectionSlipSubmit.setI_otc_id(otc_id);
    //     collectionSlipSubmit.setI_action("Collection Slip Submitted");
    //     collectionSlipSubmit.setI_otc_status("Pending OTC");
    //     collectionSlipSubmit.setI_dt_action(payment.getDt_created());
    //     collectionSlipSubmit.setI_counter_id(payment.getCounter_id());
    //     collectionSlipSubmit.setI_act_by(authService.getLoginUserName());
    //     collectionSlipSubmit.setI_created_by(authService.getLoginUserName());
    //     collectionSlipSubmit.setI_modified_by(authService.getLoginUserName());

    //     result = otcCollectionReceiptingRepository.sp_insotchist(collectionSlipSubmit);

    //     OTCollectionReceiptingRequest otCollectionReceiptingRequest = new OTCollectionReceiptingRequest();
    //     otCollectionReceiptingRequest.setI_mtt_id(insertRequest.getI_mtt_id());

    //     List<OTCCollectionReceiptingPymtItem> paymentItems = spService.sp_otccrpymtitembymtt(otCollectionReceiptingRequest);
    //     // Generate Receipt
    //     // File pdfRcpt = receiptGenerator.generateReceipt(new ReceiptRequest(pG, payment, rcpt, paymentItems, "pdf"));
    //     File pdfRcpt = receiptGenerator.generateReceipt(new OTCollectionReceiptRequest(payment, otcRcpt, paymentItems, "pdf"));

	// 	Timestamp timestamp2 = Timestamp.valueOf(LocalDateTime.now());
    //     OTCHistReq emailPending = new OTCHistReq();
    //     emailPending.setI_mtt_id(insertRequest.getI_mtt_id());
    //     emailPending.setI_otc_id(otc_id);
    //     emailPending.setI_action("Email Pending");
    //     emailPending.setI_otc_status("Email Pending");
    //     emailPending.setI_dt_action(timestamp2);
    //     emailPending.setI_counter_id(payment.getCounter_id());
    //     emailPending.setI_act_by(authService.getLoginUserName());
    //     emailPending.setI_created_by(authService.getLoginUserName());
    //     emailPending.setI_modified_by(authService.getLoginUserName());

    //     result = otcCollectionReceiptingRepository.sp_insotchist(emailPending);

    //     // Send payment email
    //     String body = "Entity Name: " + payment.getCust_nm()
    //                         + "<br>Receipt No: " + otcRcpt.getRcptNo().toUpperCase()
    //                         + "<br>Order Reference No.: " + payment.getOrn_no().toUpperCase()
    //                         + "<br>Total Amount Paid: RM" + String.format("%.2f", payment.getTotal_amt().doubleValue())
    //                         + "<br><br>Dear Sir/Madam,<br>We are pleased to inform you that your "
    //                         + "payment made in counter has been successfully processed. An official payment receipt "
    //                         + "has been generated for your records. Please find the attached receipt for "
    //                         + "your reference.<br>Thank you for using our services.<br><br><br>Tuan/Puan,<br>"
    //                         + "Dengan hormatnya,<br>Kami berbesar hati ingin memaklumkan bahawa pembayaran "
    //                         + "dalam talian anda telah berjaya diproses. Bersama-sama ini disertakan resit "
    //                         + "pembayaran untuk perhatian selanjutnya pihak Tuan/Puan.\r<br>Terima kasih kerana"
    //                         + " menggunakan perkhidmatan kami.<br><br><br><br>[THIS IS AN AUTOMATED MESSAGE - PLEASE "
    //                         + "DO NOT REPLY DIRECTLY TO THIS EMAIL]<br>";

    //                 // save email object into db
    //                 Email email = new Email("Receipt", payment.getCust_email(), "", "",
    //                         "PAYMENT SUCCESSFUL - RECEIPT ATTACHED", body);
    //                 EmailWithAttachment emailWithAttachment = new EmailWithAttachment(email, pdfRcpt);

    //                 // save and send email
    //                 emailWithAttachment = emailService.saveEmailWithAttDets(emailWithAttachment);
    //                 Boolean emailSent = false;

    //                 try {
    //                     // emailService.sendMail(email);
    //                     emailService.sendMailWithAttachment(emailWithAttachment, true);
    //                     emailSent = true;
	// 					Timestamp timestamp3 = Timestamp.valueOf(LocalDateTime.now());
    //                     OTCHistReq paid = new OTCHistReq();
    //                     paid.setI_mtt_id(insertRequest.getI_mtt_id());
    //                     paid.setI_otc_id(otc_id);
    //                     paid.setI_action("Payment Received");
    //                     paid.setI_otc_status("Paid");
    //                     paid.setI_dt_action(timestamp3);
    //                     paid.setI_counter_id(payment.getCounter_id());
    //                     paid.setI_act_by(authService.getLoginUserName());
    //                     paid.setI_created_by(authService.getLoginUserName());
    //                     paid.setI_modified_by(authService.getLoginUserName());
                
    //                     result = otcCollectionReceiptingRepository.sp_insotchistupdmtt(paid);

    //                 } catch (Exception e) {
    //                     log.error(e.getMessage(), e);
    //                     emailSent = false;
    //                     email.setRetryCnt(1);
    //                     emailService.saveEmailDets(email);
    //                 } finally {
    //                     if (emailSent) {
    //                         // 'S' = Sent
    //                         email.setStatus("S");
    //                         // update email status into db
    //                         emailService.saveEmailDets(email);

	// 						Timestamp timestamp4 = Timestamp.valueOf(LocalDateTime.now());
    //                         OTCHistReq emailsent = new OTCHistReq();
    //                         emailsent.setI_mtt_id(insertRequest.getI_mtt_id());
    //                         emailsent.setI_otc_id(otc_id);
    //                         emailsent.setI_action("Email Sent");
    //                         emailsent.setI_otc_status("Email Sent");
    //                         emailsent.setI_dt_action(timestamp4);
    //                         emailsent.setI_counter_id(payment.getCounter_id());
    //                         emailsent.setI_act_by(authService.getLoginUserName());
    //                         emailsent.setI_created_by(authService.getLoginUserName());
    //                         emailsent.setI_modified_by(authService.getLoginUserName());
                    
    //                         result = otcCollectionReceiptingRepository.sp_insotchist(emailsent);
    //                     }

    //                     // upload to idaman start
    //                     // create guid
    //                     UUID uuid = UUID.randomUUID();
    //                     String guid = "RMS-" + uuid.toString();

    //                     byte[] fileContent = Files.readAllBytes(Paths.get(pdfRcpt.toString()));
    //                     String encodedString = Base64.getEncoder().encodeToString(fileContent);

    //                     String formatedDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    //                     // upload to idaman
    //                     Integer result1 = uploadIdamanAPI(
    //                             new IdamanAPIUploadReq("RMS", otcRcpt.getRcptNo(), "RMSReceipt", formatedDate,
    //                                     "", "", "", "", "", "", guid, payment.getOrn_no(), "", "", "", "", "", "",
    //                                     encodedString, pdfRcpt.getName()),
    //                                     otcRcpt.getOtc_rcpt_id(), pdfRcpt.getName());
    //                 }

	// 	log("ReadFromCOM:- LRC OK");
	// 	log("ReadFromCOM:- Write <ACK>");
	// }

	// private Integer uploadIdamanAPI(IdamanAPIUploadReq req, Integer otcRcptID, String file_nm) throws IOException {
    //     List<IdamanAPIUpload> result = Collections.emptyList();
    //     Integer result1 = -1;

    //     // try {
    //     result = idamanAPIUploadService.idaman_api_uploadDoc(req);
    //     // if (result.size() > 0) {
    //     if (CollectionUtils.size(result) > 0) {
    //         // update rcpt table
    //         result1 = spService.sp_updotcrcpt(otcRcptID, result.get(0).getVerid(), req.getSourceSysDocRefID(), file_nm);
    //         return result1;// result.get(0).getDocRefID();
    //     }
    //     return result1;
    // }

	public static void displayC201(String transactionString){

        String response = transactionString.substring(1, 5);
        System.out.println("Response : " + response);

        String accountNumber = transactionString.substring(5, 17);
        System.out.println("Account Number : " + accountNumber);

        String statusCode = transactionString.substring(17, 19);
        System.out.println("Status Code : " + statusCode);

        String approvalCode = transactionString.substring(19, 25);
        System.out.println("Approval Code : " + approvalCode);

        String rrn = transactionString.substring(25, 37);
        System.out.println("RRN : " + rrn);

        String transactionTrace = transactionString.substring(37, 43);
        System.out.println("Transaction Trace : " + transactionTrace);

        String batchNumber = transactionString.substring(43, 49);
        System.out.println("Batch Number : " + batchNumber);

        String hostNo = transactionString.substring(49, 51);
        System.out.println("Host No : " + hostNo);

		log("ReadFromCOM:- LRC OK");
		log("ReadFromCOM:- Write <ACK>");
	}

	public static void displayC500(String transactionString){
		String response = transactionString.substring(3, 7);
        System.out.println("Response : " + response);

        String hostNO = transactionString.substring(7, 9);
        System.out.println("Host No : " + hostNO);

        String statusCode = transactionString.substring(9, 11);
        System.out.println("Status Code : " + statusCode);

        String batchNo = transactionString.substring(11, 17);
        System.out.println("Batch Number : " + batchNo);

        String batchCount = transactionString.substring(17, 20);
        System.out.println("Batch Count: " + batchCount);

        String batchAmount = transactionString.substring(20, 32);
        System.out.println("Batch Amount : " + batchAmount);

		log("ReadFromCOM:- LRC OK");
		log("ReadFromCOM:- Write <ACK>");
	}

    // Placeholder for logging method
	public static void log(String message) {
		System.out.println(message);
		logger.info(message);
	}

	// Function to set up logging
    private static void setupLogger() {
        try {
            // Get today's date in yyyyMMdd format
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String dateString = today.format(formatter);
            
            // Create a FileHandler to write log messages to a file named with today's date
            String fileName = "L_P_" + dateString + ".txt";
            FileHandler fileHandler = new FileHandler("C:/ECR_LOG/" + fileName, true); // Append mode
            
            // Set the formatter for the file handler
            fileHandler.setFormatter(new CustomFormatter());
            
            // Add the FileHandler to the logger
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	// Custom Formatter to include time in hh:mm:ss a format
	static class CustomFormatter extends Formatter {
		@Override
		public String format(LogRecord record) {
		// Get the current time in hh:mm:ss AM/PM format
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");

		// Convert milliseconds to an Instant and then to LocalDateTime
		LocalDateTime logTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(record.getMillis()), ZoneId.systemDefault());

		// Format the log record with only time and message
		String time = logTime.format(timeFormatter);
		// Format the log record with time and message
		
		return time + " " + record.getMessage() + "\n";
		}
	}

}
