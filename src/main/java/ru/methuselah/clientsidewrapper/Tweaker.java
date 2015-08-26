package ru.methuselah.clientsidewrapper;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import ru.methuselah.authlib.links.LinksMethuselah;
import ru.methuselah.securitylibrary.Data.MessagesWrapper.MessageWrappedGame;
import ru.methuselah.securitylibrary.Hacks.HacksApplicator;
import ru.methuselah.securitylibrary.WrappedGameStarter;

public class Tweaker implements ITweaker
{
	private WrappedGameStarter instance = null;
	public Tweaker()
	{
		try
		{
			final ClassLoader systemCL = ClassLoader.getSystemClassLoader();
			for(Method method : Class.forName(Wrapper.class.getCanonicalName(), true, systemCL).getMethods())
				if(method.getReturnType().equals(WrappedGameStarter.class))
				{
					instance = (WrappedGameStarter)method.invoke(null, new Object[] {});
					System.out.println("Successfully found wrapper instance...");
					break;
				}
		} catch(ClassNotFoundException ex) {
			System.err.println(ex);
		} catch(IllegalAccessException ex) {
			System.err.println(ex);
		} catch(IllegalArgumentException ex) {
			System.err.println(ex);
		} catch(InvocationTargetException ex) {
			System.err.println(ex);
		} catch(NullPointerException ex) {
			System.err.println(ex);
		}
	}
	@Override
	public void acceptOptions(List<String> args, File gameDirectory, File assetsDirectory, String profile)
	{
	}
	@Override
	public void injectIntoClassLoader(LaunchClassLoader classLoader)
	{
		if(instance != null)
		{
			System.out.println("[Methuselah] Found saved CSW instance!");
			final MessageWrappedGame message = instance.getMessage();
			if(message != null)
			{
				System.out.println("[Methuselah] Injecting into minecraft client...");
				HacksApplicator.process(message, classLoader);
				return;
			}
		}
		System.out.println("[Methuselah] Saved CSW instance is not found or message is empty!.");
		System.out.println("[Methuselah] Injecting default links provider...");
		HacksApplicator.process(new LinksMethuselah().buildReplacements(), classLoader);
	}
	@Override
	public String getLaunchTarget()
	{
		return "net.minecraft.client.main.Main";
	}
	@Override
	public String[] getLaunchArguments()
	{
		return new String[] {};
	}
}
