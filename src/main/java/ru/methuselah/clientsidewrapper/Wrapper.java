package ru.methuselah.clientsidewrapper;

import ru.methuselah.securitylibrary.Data.MessagesWrapper.MessageWrappedGame;
import ru.methuselah.securitylibrary.SecureConnection;
import ru.methuselah.securitylibrary.WrappedGameStarter;

public final class Wrapper extends WrappedGameStarter
{
	private static final int timeoutMSec = 1000 * 30;
	private static final int granuleMSec = 1000 * 2;
	public MessageWrappedGame receiveMessageFromLauncher(int port)
	{
		try
		{
			final SecureConnection connection = new SecureConnection(null, port);
			connection.start();
			System.out.println("Connecting to the launcher (" + port + ")...");
			for(int interval = 0; !Thread.interrupted() && (interval * granuleMSec < timeoutMSec); Thread.sleep(granuleMSec), interval += 1)
				if(connection.isConnected())
				{
					connection.getWrapper().writeLine("wrapper2launcher");
					return connection.getWrapper().readObject(MessageWrappedGame.class);
				} else
					System.out.print(".");
		} catch(InterruptedException ex) {
			System.err.println(ex);
		} catch(RuntimeException ex) {
			System.err.println(ex);
		}
		return null;
	}
	private void run(String[] args)
	{
		// Test for right code location
		final String myOwnPath = myOwnURL.getPath().toLowerCase();
		if(!myOwnPath.endsWith(".jar") && !myOwnPath.endsWith(".exe"))
		{
			System.err.println("Startup wrapper error: bad source location! (1)");
			System.exit(1);
		}
		MessageWrappedGame msg = null;
		if(args.length == 2 && "--port".equalsIgnoreCase(args[0]))
		{
			try
			{
				// Connect to the launcher and receive starting parameters thought TCP/SSL
				final int localLauncherPort = Integer.parseInt(args[1]);
				msg = receiveMessageFromLauncher(localLauncherPort);
				if(msg == null)
				{
					System.err.println("Startup wrapper error: cannot receive data from local launcher instance! (2-1)\n"
						+ "Please check if the firewall is blocking it.");
					System.exit(2);
				}
			} catch(NumberFormatException ex) {
				System.err.println("Startup wrapper error: wrong command line! (2)");
				System.exit(2);
			}
		}
		if(msg == null)
		{
			msg = new MessageWrappedGame();
			msg.libraries = System.getProperty("java.class.path").split("\\;");
			msg.arguments = args;
		}
		msg.tweakerClass = Tweaker.class.getCanonicalName();
		System.exit(startGameInCurrentProcess(msg));
	}
	public static void main(String[] args)
	{
		final Wrapper wrapper = new Wrapper();
		instance = wrapper;
		wrapper.run(args);
	}
}
