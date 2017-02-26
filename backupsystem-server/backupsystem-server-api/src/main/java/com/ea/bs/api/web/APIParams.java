package com.ea.bs.api.web;

/**
 * Qui definiamo le costanti da utilizzare nelle annotation, come da linee guida
 *
 */
public class APIParams {
	
	//Variabili da valorizzare  [start]
	private static final String NomeLogicoServizio_FIRST = "First";//Deve iniziare con una lettera maiuscola
	private static final String MajorVersion = "1";
	private static final String MinorVersion = "0";
	private static final String DominioApplicativo = "EA";//Deve iniziare con una lettera maiuscola
	//Variabili da valorizzare  [end]

	public static final String EJB_NAME_FIRST = NomeLogicoServizio_FIRST + "WS";
	public static final String EJB_DESC_FIRST = "Stateless Bean per il WS " + NomeLogicoServizio_FIRST;
	public static final String PORTTYPE_NAME_FIRST = NomeLogicoServizio_FIRST + "PortType_v" + MajorVersion;
	public static final String SERVICE_NAME_FIRST = NomeLogicoServizio_FIRST + "Service_v" + MajorVersion;
	public static final String PORT_NAME_FIRST = NomeLogicoServizio_FIRST + "ServicePort";

	public static final String DEFINITION_NAMESPACE = "http://" + DominioApplicativo 
													+ ".service."+NomeLogicoServizio_FIRST
													+".com/v" + MajorVersion;
	public static final String VERSION = MajorVersion + "." + MinorVersion;
	public static final String XSD_CUSTOM_NAMESPACE = DEFINITION_NAMESPACE;
	
}
