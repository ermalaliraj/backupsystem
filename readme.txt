set "JAVA_HOME=C:\Program Files\Java\jdk1.8.0_101"

cd C:/Program Files/jboss-as-7.1.1.Final/bin  (C:/Program Files/jboss-wildfly-10.1/bin)
jboss-cli.bat --connect controller=localhost:8990
deploy D:\Ermal_Data\Projects\examples_accademic\backupsystem\backupsystem-server\backupsystem-server-ear\target\backupsystem-server-ear.ear
copy "D:\Ermal_Data\Projects\examples_accademic\backupsystem\backupsystem-server\backupsystem-server-ear\target\backupsystem-server-ear.ear" "C:\Program Files\jboss-wildfly-10.1\standalone\deployments"

cd C:/Program Files/jboss-as-7.1.1.Final_RU/bin    (C:\Program Files\jboss-wildfly-10.1_RU\bin)
jboss-cli.bat --connect controller=localhost:9990
deploy D:\Ermal_Data\Projects\examples_accademic\backupsystem\backupsystem-ru\backupsystem-ru-ear\target\backupsystem-ru-ear.ear
cp /home/uel/workspace/backupsystem/backupsystem-ru/backupsystem-ru-ear/target/backupsystem-ru-ear.ear /opt/wildfly/standalone/deployments/

rm -rf /opt/wildfly/standalone/deployments/*


Error SOLUTIONS:
1)  Caused by: org.jboss.msc.service.DuplicateServiceException: Service jboss.pojo.\"org.jboss.netty.internal.LoggerConfigurator\".DESCRIBED is already registered"},
From standalone.xml remove pojo
<extension module="org.jboss.as.pojo"/>
<subsystem xmlns="urn:jboss:domain:pojo:1.0"/>

2) [RU] GeneralSecurityException when starting certifications: java.security.NoSuchProviderException: no such provider: 
on: keyStore = KeyStore.getInstance("pkcs12", "BC");
Sol: 
BSSecurityDomain
@PostConstruct
public void init() throws Exception {
	Security.addProvider(new BouncyCastleProvider());
}

3) [RU] error constructing MAC: java.security.NoSuchProviderException: JCE cannot authenticate the provider BC
on: keyStore.load(new ByteArrayInputStream(authCertificate), authCertificatePwd.toCharArray());
Created org\bouncycastle\main following: 
https://developer.jboss.org/thread/175395
http://stackoverflow.com/questions/9534512/bouncycastle-jboss-as7-jce-cannot-authenticate-the-provider-bc

4) Exception in thread "main" java.lang.ClassFormatError: Absent Code attribute in method that is not native or abstract in class file javax/ejb/NoSuchEJBException
Add jboss-client.jar to classpath of client project (GUI)

5) when properties.put(Context.PROVIDER_URL, "http-remoting://" + host + ":" + port);
Caused by: org.jboss.remoting3.UnknownURISchemeException: No connection provider for URI scheme "http-remoting" is installed
<dependency>
	<groupId>org.wildfly</groupId>
	<artifactId>wildfly-ejb-client-bom</artifactId>
	<version>10.1.0.Final</version>
</dependency>
<dependency>
	<groupId>org.wildfly</groupId>
	<artifactId>wildfly-jms-client-bom</artifactId>
	<version>10.1.0.Final</version>
</dependency>

5) Sending remote messages
http://stackoverflow.com/questions/9588166/sending-a-jms-message-to-a-remote-queue-on-jboss-as-5-and-as-7?rq=1
https://developer.jboss.org/message/721977#721977#721977
http://docs.jboss.org/hornetq/2.2.5.Final/user-manual/en/html_single/index.html#d0e8392
https://lukaszantoniak.wordpress.com/2012/12/11/jboss-as-7-remote-jms-queue/
https://developer.jboss.org/thread/195968
https://developer.jboss.org/thread/195845

https://developer.jboss.org/thread/169712
https://developer.jboss.org/thread/196472

BUG for the dynamic functionality
https://issues.jboss.org/browse/EJBCLIENT-12


git branch basic
git checkout basic
git add .
git push

git push --set-upstream origin basic
git diff master basic

git log -p -1
git log --stat

git checkout master
git merge basic

--cancel local changes
git clean -df
git checkout -- .





Util links:
http://middlewaremagic.com/jboss/?tag=jboss-client-7-1-0-final-jar

http://palkonyves.blogspot.it/2013/04/exceptions-and-transactions-in-ejb.html
http://entjavastuff.blogspot.it/2011/02/ejb-transaction-management-going-deeper.html
https://issues.jboss.org/browse/AS7-4217
https://docs.jboss.org/jbossas/jboss4guide/r4/html/ch7.chapt.html
https://mirocupak.com/duplicate-services-and-the-pojo-subsystem-in-wildfly/
http://slideplayer.it/slide/569325/
https://books.google.it/books?id=mV2N6h4n1pcC&printsec=frontcover&hl=it#v=onepage&q&f=false
https://www.atlassian.com/git/tutorials/setting-up-a-repository

http://docs.jboss.org/hornetq/2.2.5.Final/user-manual/en/html/paging.html

https://issues.jboss.org/browse/HORNETQ-286
http://stackoverflow.com/questions/7827919/hornetq-jmsexception-failed-to-create-session-factory

WILDFLY
https://docs.jboss.org/author/display/WFLY10/Messaging+configuration
https://docs.jboss.org/author/display/WFLY10/Connect+a+pooled-connection-factory+to+a+Remote+Artemis+Server
https://developer.jboss.org/message/724894#724894
https://blogs.oracle.com/arungupta/entry/jmscontext_jmsdestinationdefintion_defaultjmsconnectionfactory_with_simplified
https://access.redhat.com/documentation/en-US/JBoss_Enterprise_Application_Platform/6.3/html/Administration_and_Configuration_Guide/Configure_a_Generic_JMS_Resource_Adapter_for_Use_with_a_Third-party_JMS_Provider.html

todo:
1) get cf = (ConnectionFactory) ctx.lookup("jms/RemoteConnectionFactory");  on RU. why not working in RU?


<connector name="remote-jms">
	<factory-class>org.hornetq.core.remoting.impl.netty.NettyConnectorFactory</factory-class>
	<param key="host" value="127.0.0.1"/>
	<param key="port" value="4445"/>
</connector>

connectionFactory: HornetQConnectionFactory [serverLocator=ServerLocatorImpl [initialConnectors=[org-hornetq-core-remoting-impl-netty-NettyConnectorFactory?port=4445&host=localhost], discoveryGroupConfiguration=null], clientID=null, dupsOKBatchSize=1048576, transactionBatchSize=1048576, readOnly=false]

javax.naming.NamingException: Failed to lookup [Root exception is java.io.NotSerializableException: org.hornetq.core.client.impl.ClientSessionFactoryImpl]



https://docs.jboss.org/author/display/WFLY8/EJB+invocations+from+a+remote+server+instance
https://developer.jboss.org/thread/261163
https://developer.jboss.org/thread/237145
https://developer.jboss.org/thread/207725
https://developer.jboss.org/thread/155233
https://developer.jboss.org/thread/169780 (quesot )

http://archive.oreilly.com/pub/a/onjava/excerpt/jms_ch2/index.html?page=3
