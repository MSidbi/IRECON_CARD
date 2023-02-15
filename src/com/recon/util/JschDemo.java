package com.recon.util;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class JschDemo {
	
	
	public static void main(String[] args) {
		String SFTPHOST = "10.143.136.142";
		int    SFTPPORT = 22;
		String SFTPUSER = "admn2";
		String SFTPPASS = "M@sterkey@123";
		String SFTPWORKINGDIR = "/user1/admin/testwork"; 
		Session     session     = null;
		Channel     channel     = null;
		ChannelSftp channelSftp = null; 
		try{   
			
			
			 	JSch jsch=new JSch();
			 	 session = jsch.getSession(SFTPUSER, SFTPHOST, 22);
			 	java.util.Properties config = new java.util.Properties();
	            config.put("StrictHostKeyChecking", "no");

		            session.setConfig(config);
		            session.setPassword(SFTPPASS);
		            session.connect();

		             channel = session.openChannel("sftp");
		            channel.connect();
		            ChannelSftp sftpChannel = (ChannelSftp) channel;
		            sftpChannel.get("//user1/admin/testwork/SWT_29032017.txt", "C:\\Users\\Int6261\\Desktop\\localfile.txt");
		            sftpChannel.exit();
		            session.disconnect();

		       
				/*JSch jsch = new JSch();            
				session = jsch.getSession(SFTPUSER,SFTPHOST,SFTPPORT);            
				session.setPassword(SFTPPASS);   
				session.setConfig(
					    "PreferredAuthentications", 
					    "publickey,keyboard-interactive,password");
				java.util.Properties config = new java.util.Properties();            
				config.put("StrictHostKeyChecking", "no");            
				session.setConfig(config);           
				session.connect();            
				channel = session.openChannel("sftp");            
				channel.connect();            
				channelSftp = (ChannelSftp)channel;            
				channelSftp.cd(SFTPWORKINGDIR);           
				File f = new File("\\\\10.144.133.245\\led\\working\\SWT_29032017.txt");            
				channelSftp.put(new FileInputStream(f), f.getName());
			 */
		}catch(Exception ex){
				ex.printStackTrace();
				
			} 
		}
}

/*@SpringBootApplication
 class SftpJavaApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SftpJavaApplication.class).web(false).run(args);
    }

    @Bean
    public SessionFactory<LsEntry> sftpSessionFactory() {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
        factory.setHost("...");
        factory.setPort(22);
        factory.setUser("...");
        factory.setPassword("...");
        factory.setAllowUnknownKeys(true);
        return new CachingSessionFactory<LsEntry>(factory);
    }

    @Bean
    public SftpInboundFileSynchronizer sftpInboundFileSynchronizer() {
        SftpInboundFileSynchronizer fileSynchronizer = new SftpInboundFileSynchronizer(sftpSessionFactory());
        fileSynchronizer.setDeleteRemoteFiles(false);
        fileSynchronizer.setRemoteDirectory("foo");
        fileSynchronizer.setFilter(new SftpSimplePatternFileListFilter("*.txt"));
        return fileSynchronizer;
    }

    @Bean
    @InboundChannelAdapter(channel = "sftpChannel", poller = @Poller(fixedDelay = "5000"))
    public MessageSource<File> sftpMessageSource() {
        SftpInboundFileSynchronizingMessageSource source = new SftpInboundFileSynchronizingMessageSource(
                sftpInboundFileSynchronizer());
        source.setLocalDirectory(new File("ftp-inbound"));
        source.setAutoCreateLocalDirectory(true);
        source.setLocalFilter(new AcceptOnceFileListFilter<File>());
        return source;
    }

    @Bean
    @ServiceActivator(inputChannel = "sftpChannel")
    public MessageHandler handler() {
        return new MessageHandler() {

            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                System.out.println(message.getPayload());
            }

        };
    }

}*/
