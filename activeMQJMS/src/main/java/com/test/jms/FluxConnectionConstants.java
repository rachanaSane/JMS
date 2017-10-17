package com.test.jms;/* Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

public class FluxConnectionConstants {

    private FluxConnectionConstants(){}

    public static final String DESTINATION_TYPE_QUEUE = "javax.jms.Queue";
    public static final String FLUX_MDR_REMOTE_MESSAGE_IN_QUEUE = "java:/jms/queue/mdrin";
    public static final String MDROUT_QUEUE_WITHOUT_COLON_JAVA = "jms/queue/mdr2out";
    public static final String BRIDGE_QUEUE_WITHOUT_COLON_JAVA = "jms/queue/bridge";
    public static final String FLUX_MDR_REMOTE_MESSAGE_IN_QUEUE_NAME = "mdrin";
    public static final String FLUX_CONNECTION_FACTORY = "java:/FluxFactory";

    // ConnectionFactory details
    public static final String REMOTE_CONNECTION_FACTORY = "java:/jms/RemoteConnectionFactory";
    public static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    public static final String URL_W_PORT_TO_REMOTE_JMS = "http-remoting://10.155.0.10:8180";
    public static final String FLUX_ID = "fluxq";
    public static final String FLUXP = "testpassword";

    // Queue details
    public static final String JMS_QUEUE_BRIDGE = "java:/jms/queue/bridge";

    // Message details
    public static final String CONNECTOR_ID = "CONNECTOR_ID";
    public static final String CONNECTOR_ID_VAL = "JMS MDM Business AP1";

    public static final String FLUX_ENV_AD = "AD";
    public static final String FLUX_ENV_AD_VAL = "XEU";

    public static final String FLUX_ENV_DF = "DF";
    public static final String FLUX_ENV_DF_VAL = "urn:un:unece:uncefact:fisheries:FLUX:MDM:EU:2";

    public static final String FLUX_ENV_TO = "TO";
    public static final String FLUX_ENV_TO_VAL = "20";

    public static final String FLUX_ENV_TODT = "TODT";

    public static final String RE = "RE";
    public static final String RE_VAL = "XEU";

    public static final String FLUX_ENV_AR = "AR";
    public static final String FLUX_ENV_AR_VAL = "True";

    public static final String CT = "CT";
    public static final String CT_VAL = "andrei.dub@arhs-dev-bru.com";

    // Business procedure signature
    public static final String BUSINESS_PROCEDURE_PREFIX = "BP";
    public static final String BUSINESS_UUID = "BUSINESS_UUID";

}

