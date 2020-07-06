package com.zoho.ifsc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class IFSC_Getter
{

	public static void main(String[] args) throws Exception
	{
		ArrayList<String> ifsc_check = new ArrayList<>();
		FileWriter fw=new FileWriter("NEFT_valid.xls");
		fw.write("BANK NAME"+"\t"+"IFSC"+"\t"+"BRANCH"+"\n");
		FileWriter fw1=new FileWriter("NEFT_invalid.xls");
		fw1.write("BANK NAME"+"\t"+"IFSC"+"\t"+"BRANCH"+"\n");
		try {
			InputStream is = new FileInputStream(new File("NEFT_Check.xlsx"));
	
			XSSFWorkbook wb=new XSSFWorkbook(is);
			// InputStream or File for XLSX file (required)
			int i = 0;
			int count=1;
	
			while (i < wb.getNumberOfSheets())
			{
				XSSFSheet sheet = wb.getSheetAt(i);		
				for (int j=1;j<=sheet.getLastRowNum();j++)
				{
					Row r = sheet.getRow(j);
					ArrayList<String> temp = new ArrayList<>();
					for (Cell cell : r)
					{
						String value = "";
						switch (cell.getCellType())
						{
							case STRING:
								value = cell.getRichStringCellValue().getString();
								break;
							case NUMERIC:
								if (DateUtil.isCellDateFormatted(cell))
								{
									value = cell.getDateCellValue().toString();
								}
								else
								{
									value = String.valueOf(cell.getNumericCellValue());
								}
								break;
							case BOOLEAN:
								value = String.valueOf(cell.getBooleanCellValue());
								break;
							default:
								System.out.println("Invalid value");
						}
						temp.add(value);
					}
					String ifsc = temp.get(1);
					String str = temp.get(0) + "\t" + temp.get(1) + "\t" + temp.get(3);
					if (ifsc.trim().length() == 11 && !(ifsc_check.contains(ifsc.trim())))
					{
						fw.write(str + "\n");
						ifsc_check.add(ifsc.trim());
					}
					else if (ifsc.length() == 11)
					{
						fw1.write(str + "\t"+"Already Exist"+"\n");
					}
					else
					{
						fw1.write(str + "\n");
					}
					System.out.println("NEFT Sheet : "+i+" Count: "+ count++ +" "+ str);
				}
				i++;
			}
			fw.close();
			fw1.close();
			is.close();
			wb.close();
			System.out.println("\n\n\n ****************** NEFT COMPLETED *********************");
			fw=new FileWriter("RTGS_valid.xls");
			fw.write("BANK NAME"+"\t"+"IFSC"+"\t"+"BRANCH"+"\n");
			fw1=new FileWriter("RTGS_invalid.xls");
			fw1.write("BANK NAME"+"\t"+"IFSC"+"\t"+"BRANCH"+"\n");
			is = new FileInputStream(new File("RTGS_Check.xlsx"));
			wb=new XSSFWorkbook(is);
			i = 0;
			count=1;
			while (i < wb.getNumberOfSheets())
			{
				XSSFSheet sheet = wb.getSheetAt(i);		
				for (int j=1;j<=sheet.getLastRowNum();j++)
				{
					Row r = sheet.getRow(j);
					ArrayList<String> temp = new ArrayList<>();
					for (Cell cell : r)
					{
						String value = "";
						switch (cell.getCellType())
						{
							case STRING:
								value = cell.getRichStringCellValue().getString();
								break;
							case NUMERIC:
								if (DateUtil.isCellDateFormatted(cell))
								{
									value = cell.getDateCellValue().toString();
								}
								else
								{
									value = String.valueOf(cell.getNumericCellValue());
								}
								break;
							case BOOLEAN:
								value = String.valueOf(cell.getBooleanCellValue());
								break;
							default:
								break;
						}
						value = value.replaceAll("[^a-zA-Z0-9 ]","");
						temp.add(value);
					}
					String ifsc = temp.get(1);
					String str = temp.get(0) + "\t" + temp.get(1).trim() + "\t" + temp.get(2);
					if (ifsc.trim().length() == 11 && !(ifsc_check.contains(ifsc.trim())))
					{
						fw.write(str + "\n");
						ifsc_check.add(ifsc.trim());
					}
					else if (ifsc.length() == 11)
					{
						fw1.write(str + "\t"+"Already Exist"+"\n");
					}
					else
					{
						fw1.write(str + "\n");
					}
					System.out.println("RTGS Sheet : "+i+" Count: "+ count++ +" "+ str);
				}
				i++;
			}
			fw.close();
			fw1.close();
			is.close();
			wb.close();
			System.out.println("\n\n\n ****************** RTGS COMPLETED *********************");
			TrustManager[] trustAllCerts = new TrustManager[]{
			        new X509TrustManager() {
			            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			                return null;
			            }

			            public void checkClientTrusted(
			                    java.security.cert.X509Certificate[] certs, String authType) {
			            }

			            public void checkServerTrusted(
			                    java.security.cert.X509Certificate[] certs, String authType) {
			            }
			        }
			};

			// Install the all-trusting trust manager
			try {
			    SSLContext sc = SSLContext.getInstance("SSL");
			    sc.init(null, trustAllCerts, new java.security.SecureRandom());
			    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			} catch (Exception e) {
			    System.out.println("Error" + e);
			}

			// Now you can access URL(https) without having the certificate in the truststore
			try {

			    HostnameVerifier hv = new HostnameVerifier() {
			        public boolean verify(String urlHostName, SSLSession session) {
			            System.out.println("Warning: URL Host: " + urlHostName + " vs. "
			                    + session.getPeerHost());
			            return true;
			        }
			    };

			    String datam = "param=myparam";
			    URL url = new URL("https://www.npci.org.in/national-automated-clearing-live-members-1");
			    URLConnection conn = url.openConnection();
			    HttpsURLConnection urlConn = (HttpsURLConnection) conn;
			    urlConn.setHostnameVerifier(hv);
			    conn.setDoOutput(true);
			    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			    wr.write(datam);
			    wr.flush();

			    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			    StringBuilder sb = new StringBuilder();
			    String inputLine;
			    while ((inputLine = in.readLine()) != null) {
			        sb.append(inputLine);
			    }
			    in.close();
			    String res = sb.toString();
			    fw=new FileWriter("NACH_valid.xls");
				fw1=new FileWriter("NACH_invalid.xls");
				fw.write("Bank Code\tBank Name\tIFSC Code\n");
				fw1.write("Bank Code\tBank Name\tIFSC Code\n");
			    Document doc = Jsoup.parse(res);
			    Element table = doc.select("table").get(0);
			    Iterator<Element> ite = table.select("td").iterator();
			    i=0;
			    while(ite.hasNext())
			    {
			    	ArrayList<String> temp = new ArrayList<>();
			    	ite.next();
			        temp.add(ite.next().text());
			        temp.add(ite.next().text());
			        temp.add(ite.next().text());
			        temp.add(ite.next().text());
			        temp.add(ite.next().text());
			        temp.add(ite.next().text());
			        temp.add(ite.next().text());
			        temp.add(ite.next().text());
			        temp.add(ite.next().text());
			        ite.next();
			        String ifsc = temp.get(3);
			        if(temp.get(3).length()==11 && !(ifsc_check.contains(ifsc.trim())))
			        {
			        	fw.write(temp.get(0)+"\t"+temp.get(1)+"\t"+temp.get(3)+"\n");
			        	ifsc_check.add(ifsc);
			        }
			        else if(temp.get(3).length()==11)
			        {
			        	fw1.write(temp.get(0)+"\t"+temp.get(1)+"\t"+temp.get(3)+"\t"+"Already Exists"+"\n");
			        }
			        else
			        {
			        	fw1.write(temp.get(0)+"\t"+temp.get(1)+"\t"+temp.get(3)+"\n");
			        }
			        System.out.println(++i);
			    }
			    fw.close();
			    fw1.close();
			    System.out.println("\n\n\n ****************** NACH COMPLETED *********************");
			} catch (MalformedURLException e) {
			    System.out.println("Error in SLL Connetion" + e);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}