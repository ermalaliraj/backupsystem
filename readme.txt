cd C:/Program Files/jboss-as-7.1.1.Final/bin
jboss-cli.bat --connect controller=localhost:9999
deploy D:\Ermal_Data\Projects\examples_accademic\backupsystem\backupsystem-server\backupsystem-server-ear\target\backupsystem-server-ear.ear

cd C:/Program Files/jboss-as-7.1.1.Final_RU/bin
jboss-cli.bat --connect controller=localhost:8999
deploy D:\Ermal_Data\Projects\examples_accademic\backupsystem\backupsystem-ru\backupsystem-ru-ear\target\backupsystem-ru-ear.ear


Error SOLUTIONS:
1) [RU] GeneralSecurityException when starting certifications: java.security.NoSuchProviderException: no such provider: 
on: keyStore = KeyStore.getInstance("pkcs12", "BC");
Sol: 
BSSecurityDomain
@PostConstruct
public void init() throws Exception {
	Security.addProvider(new BouncyCastleProvider());
}

2) [RU] error constructing MAC: java.security.NoSuchProviderException: JCE cannot authenticate the provider BC
on: keyStore.load(new ByteArrayInputStream(authCertificate), authCertificatePwd.toCharArray());
Created org\bouncycastle\main following: 
https://developer.jboss.org/thread/175395
http://stackoverflow.com/questions/9534512/bouncycastle-jboss-as7-jce-cannot-authenticate-the-provider-bc

3) Exception in thread "main" java.lang.ClassFormatError: Absent Code attribute in method that is not native or abstract in class file javax/ejb/NoSuchEJBException
Add jboss-client.jar to classpath of client project (GUI)


4) Sending remote messages
http://stackoverflow.com/questions/9588166/sending-a-jms-message-to-a-remote-queue-on-jboss-as-5-and-as-7?rq=1
https://developer.jboss.org/message/721977#721977#721977
http://docs.jboss.org/hornetq/2.2.5.Final/user-manual/en/html_single/index.html#d0e8392
https://lukaszantoniak.wordpress.com/2012/12/11/jboss-as-7-remote-jms-queue/


git branch basic
git checkout basic
git push --set-upstream origin basic
git diff master basic

git log -p -1
git log --stat

git checkout master
git merge basic

--cancel local changes
git checkout .





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
